package ontologyManagement;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Configuration.TableauMonitorType;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.TimedConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import test.ComparisonResult;
import test.DatasetTest;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.ExplanationProgressMonitor;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;

public class MyOWLOntology {
	//private OWLOntologyManager manager;
	private OWLOntology o;
	//private Set<OWLConcept> concepts;
	private Map<String, OWLConcept> concepts;

	private Map<OWLClass, Map<OWLClass, Integer>> conceptDistances;
	private Map<String, OWLRelation> relations;
	private OWLReasoner reasoner;
	private OWLDataFactory factory;
	private ExplanationGenerator<OWLAxiom> expl;
	private String prefix;
	
	
	public MyOWLOntology(String ontFile, String pr)
	{
		concepts = new HashMap<String,OWLConcept>();
		relations = new HashMap<String,OWLRelation>();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
		conceptDistances = new HashMap<OWLClass, Map<OWLClass, Integer>>();
		prefix = pr;
    	
		
		try {
			o = manager.loadOntologyFromOntologyDocument(new File(ontFile));
			System.out.println("GOOOOL");
			startReasoner();
            System.out.println("Reasoner ready");			
			Set<OWLObjectProperty> objectProperties = o.getObjectPropertiesInSignature();
			objectProperties.remove(factory.getOWLObjectProperty(IRI.create("http://www.w3.org/2002/07/owl#topObjectProperty")));
			for (Iterator<OWLObjectProperty> i = objectProperties.iterator(); i.hasNext();)
			{
				OWLObjectProperty current = i.next();
				//relations.add(new OWLRelation(current, this));
				relations.put(current.toStringID(), new OWLRelation(current, this));
			}
			
			System.out.println("Relations read");
			
			Set<OWLClass> classes = o.getClassesInSignature();
			for (Iterator<OWLClass> i = classes.iterator(); i.hasNext();)
			{
				OWLClass current = i.next();
				//concepts.add(new OWLConcept(current, this));
				concepts.put(current.toStringID(), new OWLConcept(current, this));
			}
			classes = null; //Finished with classes
			System.out.println("Classes read");
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public OWLOntology getOWLOntology()
	{
		return o;
	}
	
	public String getOntologyPrefix()
	{
		return prefix;
	}
	
	public Set<OWLLink> getConceptOWLLink (OWLConcept c)
	{
		Set<OWLLink> ownLinks = new HashSet<OWLLink>();
		Set<OWLConcept> potentialNeighbors = getIsland(c);
		for (Iterator<OWLConcept> j = potentialNeighbors.iterator(); j.hasNext();)
		{
			OWLConcept d = j.next();
			for (Iterator<OWLRelation> k = relations.values().iterator(); k.hasNext();)
			{
				OWLRelation r = k.next();
				Set<OWLExplanation> exps = checkOWLLink(c, r, d); 
				if (exps != null)
				{
					OWLLink link = new OWLLink(r, d, exps); //All the links, inferred and not inferred, have explanations
					ownLinks.add(link);
				}
			}
		}
		return ownLinks;
	}
	
	
	private void getOWLLinks(Set<OWLConcept> classes, Set<OWLRelation> objectProperties)
	{
		double progressCounter = 0.0;
		double totalLoops = classes.size()*classes.size()*objectProperties.size();
		//In this loop we check for each concept if it has any type of relation with any other in the ontology.
		for (Iterator<OWLConcept> i = classes.iterator(); i.hasNext();)
		{
			OWLConcept c = i.next();
			Set<OWLLink> ownLinks = new HashSet<OWLLink>();
			for (Iterator<OWLConcept> j = classes.iterator(); j.hasNext();)
			{
				OWLConcept d = j.next();
				for (Iterator<OWLRelation> k = objectProperties.iterator(); k.hasNext();)
				{
					OWLRelation r = k.next();
					Set<OWLExplanation> exps = checkOWLLink(c, r, d); 
					if (exps != null)
					{
						OWLLink link = new OWLLink(r, d, exps); //All the links, inferred and not inferred, have explanations
						ownLinks.add(link);
					}
					progressCounter++;
				}
				
			}
            //Set neighbors of OWLConcepts
			c.setNeighbors(ownLinks);
			System.out.println(progressCounter*100/totalLoops + "%");
		}
	}
	
	
	private Set<OWLConcept> getIsland(OWLConcept c)
	{
		return getIsland(c, new HashSet<OWLConcept>());
	}
	
	private Set<OWLConcept> getIsland(OWLConcept c, Set<OWLConcept> visited)
	{
		
		Set<OWLConcept> island = new HashSet<OWLConcept>();
		
		Set<OWLClassExpression> superClasses = c.getOWLClass().getSuperClasses(o);
		Stack<OWLClassExpression> stck = new Stack<OWLClassExpression>();
		stck.addAll(superClasses);
		superClasses.clear();
		double size = stck.size();
		for (int i = 0; i < size; i++)//(Iterator<OWLClassExpression> i = superClasses.iterator(); i.hasNext();)
		{
			OWLClassExpression clExp = stck.pop();//i.next();
			if (clExp.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM)
			{
				OWLObjectSomeValuesFrom aux = (OWLObjectSomeValuesFrom) clExp;
				OWLClass destiny = aux.getFiller().asOWLClass();
				OWLConcept destinyConcept = getOWLConcept(destiny.toStringID()); 
				if (!visited.contains(destinyConcept))
				{
					island.add(destinyConcept);
					visited.add(destinyConcept);
					island.addAll(getIsland(destinyConcept, visited));
				}
			}
			if (clExp.getClassExpressionType() == ClassExpressionType.OWL_CLASS)
			{
				OWLConcept parentConcept = getOWLConcept(((OWLClass) clExp).toStringID()); 
				if (!visited.contains(parentConcept))
				{
					island.addAll(getIsland(parentConcept, visited));
				}
			}
		}
		return island;
	}
	
	private Set<OWLExplanation> checkOWLLink(OWLConcept c1, OWLRelation r, OWLConcept c2)
	{
		OWLClass a = c1.getOWLClass();
		OWLClass b = c2.getOWLClass();
		OWLObjectProperty p = r.getOWLObjectProperty();
        OWLObjectSomeValuesFrom relationAxiom = factory.getOWLObjectSomeValuesFrom(p, b);
        OWLSubClassOfAxiom linkAxiom = factory.getOWLSubClassOfAxiom(a, relationAxiom);
        
        //Maybe we have to consider not only the "some values from", but also "all values from"
        Set<OWLExplanation> explanations = null;
        if (o.containsAxiom(linkAxiom))
        {
        	explanations = Collections.emptySet();
        	return explanations;
        }
        if (reasoner.isEntailed(linkAxiom)) //If the axiom is explicit in the ontology does not have explanation
        {
        	explanations = new HashSet<OWLExplanation>();
        	Set<Explanation<OWLAxiom>> expAxioms = expl.getExplanations(linkAxiom, 1);
        	for (Iterator<Explanation<OWLAxiom>> i = expAxioms.iterator(); i.hasNext();)
        	{
        		OWLExplanation e;
				try {
					e = new OWLExplanation(i.next().getAxioms(), this);
					explanations.add(e);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        }
        return explanations;
	}
	
	
	private void startReasoner(){
		OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory(); //ElkReasonerFactory(); new JFactFactory(); //new PelletReasonerFactory(); // 
		Configuration configuration = new Configuration();
		configuration.ignoreUnsupportedDatatypes = true;
		configuration.throwInconsistentOntologyException = false;
		reasoner =  reasonerFactory.createReasoner(o, configuration);
        reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
        reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_ASSERTIONS);
        reasoner.precomputeInferences(InferenceType.DISJOINT_CLASSES);
        ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory);
        expl = genFac.createExplanationGenerator(o);
	}
	
	public void restartReasoner()
	{
		reasoner.dispose();
		startReasoner();
	}

	public Set<OWLConcept> getConcepts() {
		return new HashSet<OWLConcept>(concepts.values());
	}
	
	public void removeConcept(OWLConcept c)
	{
		c.dispose();
		//this.concepts.remove(c);
	}
	
	public OWLConcept getOWLConcept (String uri)
	{
		OWLConcept con = concepts.get(uri);
		if (con == null)
		{
			con = new OWLConcept(factory.getOWLClass(IRI.create(uri)), this);
			concepts.put(uri, con);
		}
		return con;
	}
	
	public Set<OWLConcept> getSubConcepts(OWLConcept c)
	{
		Set<OWLClass> classes = reasoner.getSubClasses(c.getOWLClass(), false).getFlattened();
		Set<OWLConcept> subConcepts = new HashSet<OWLConcept>();
		for (Iterator<OWLClass> i = classes.iterator(); i.hasNext();)
		{
			OWLClass cl = i.next();
			subConcepts.add(this.getOWLConcept(cl.toStringID()));
		}
		return subConcepts;
	}
	
	public OWLClass getOWLClass (String uri)
	{
		return factory.getOWLClass(IRI.create(uri));
	}
	
	public OWLNamedIndividual getOWLIndividual (String uri)
	{
		return factory.getOWLNamedIndividual(IRI.create(uri));
	}
	
	public boolean isSatisfiable(OWLClass cl)
	{
		return reasoner.isSatisfiable(cl);
	}
	
	public boolean isSubClassOf(OWLClass sub, OWLClass sup)
	{
		return reasoner.getSuperClasses(sub, false).containsEntity(sup);
	}
	
	public boolean isOfType(OWLNamedIndividual ind, OWLClass c)
	{
		return reasoner.getTypes(ind, false).containsEntity(c);
	}

	public Set<OWLRelation> getRelations() {
		return new HashSet<OWLRelation>(relations.values());
	}
	
	public OWLRelation getOWLRelation (String uri)
	{
		return relations.get(uri);
	}

	public OWLObjectProperty getOWLObjectProperty(String uri)
	{
		return factory.getOWLObjectProperty(IRI.create(uri));
	}
	
	private <T,S> T profLCS (Set<T> setX, Set<T> setY, T x, T y)
	{
		if (x == y)
			return x;
		Set<T> common = new HashSet<T>(setX);
		common.retainAll(setY);
		
		T lcs = common.iterator().next();
		int maxProf = prof(lcs);
		for (Iterator<T> i = common.iterator(); i.hasNext(); )
		{
			T aux = (T) i.next();
			
			if (prof(aux) > maxProf )
			{
				maxProf = prof(aux);
				lcs = aux;
			}
		}
		return lcs;
	}
	
	
	public double taxonomicPropertySimilarity (OWLObjectProperty x, OWLObjectProperty y)
	{	
		Set<OWLObjectPropertyExpression> setX = reasoner.getSuperObjectProperties(x, false).getFlattened();
		setX.add(x);
		Set<OWLObjectPropertyExpression> setY = reasoner.getSuperObjectProperties(y, false).getFlattened();
		setY.add(y);
		
		OWLObjectProperty lcs = (OWLObjectProperty) profLCS(setX, setY, x, y);
		double profLCS = prof(lcs);
		//OWLObjectProperty root = factory.getOWLTopObjectProperty();
		
		double dxa = dist(x, lcs);
		double dxroot = profLCS + dxa;//dist(x, root);
		double dya = dist(y, lcs);
		double dyroot = profLCS + dya;//dist(y, root);
		double dtax = (dxa + dya)/(dxroot + dyroot);
		
		return 1-dtax;
	}
	
	public OWLConcept getLCS(OWLConcept a, OWLConcept b)
	{
		OWLClass x = a.getOWLClass(), y = b.getOWLClass();
		Set<OWLClass> setX = reasoner.getSuperClasses(x, false).getFlattened();
		setX.add(x);
		Set<OWLClass> setY = reasoner.getSuperClasses(y, false).getFlattened();
		setY.add(y);
		OWLClass lcs = profLCS(setX, setY, x, y);
		return this.getOWLConcept(lcs.toStringID());
	}
	
	
	public double taxonomicClassSimilarity (OWLClass x, OWLClass y)
	{
		Set<OWLClass> setX = reasoner.getSuperClasses(x, false).getFlattened();
		setX.add(x);
		Set<OWLClass> setY = reasoner.getSuperClasses(y, false).getFlattened();
		setY.add(y);
		
		//=======================Only for ComparisonCosine
		//OWLClass obsolete = factory.getOWLClass(IRI.create("http://www.geneontology.org/formats/oboInOwl#ObsoleteClass"));
		/*OWLAnnotationProperty deprecated = factory.getOWLAnnotationProperty(IRI.create("http://www.w3.org/2002/07/owl#deprecated"));
		Set<OWLAnnotation> annX = x.getAnnotations(o, deprecated);
		if (annX.iterator().next().isDeprecatedIRIAnnotation())
			System.out.println("Deprecated");*/
		//if (setX.contains(obsolete) || setY.contains(obsolete))
		//	return 0;
		//=======================END
		
		OWLClass lcs = profLCS(setX, setY, x, y);
		double profLCS = prof(lcs);
		
		double dxa = dist(x, lcs);
		double dxroot = profLCS + dxa;//dist(x, root);
		double dya = dist(y, lcs);
		double dyroot = profLCS + dya;//dist(y, root);
		double num = dxa + dya;
		double den = dxroot + dyroot;
		double dtax = num/den;
		dtax = 1.0 - dtax;
		
		/*System.out.println(lcs +  " " + profLCS);
		System.out.println(dxa + " " + dya);
		System.out.println(x + " " + y + " " + dtax);*/
		return dtax;
	}
	
	public double taxonomicIndividualSimilarity (OWLLogicalEntity x, OWLLogicalEntity y)
	{
		Set<OWLClass> setX;
		Set<OWLClass> setY;
		OWLClass lcs = null;
		if (x.isOWLNamedIndividual() && y.isOWLNamedIndividual())
		{
			setX = reasoner.getTypes(x.asOWLNamedIndividual(), false).getFlattened();
			setY = reasoner.getTypes(y.asOWLNamedIndividual(), false).getFlattened();
			lcs = profLCS(setX, setY, setX.iterator().next(), null);
		}
		
		if (x.isOWLClass() && y.isOWLClass())
		{
			OWLClass xC = x.asOWLClass();
			OWLClass yC = y.asOWLClass();
			setX = reasoner.getSuperClasses(xC, false).getFlattened();
			setX.add(xC);
			setY = reasoner.getSuperClasses(yC, false).getFlattened();
			setY.add(yC);
			lcs = profLCS(setX, setY, xC, yC);
		}
		
		
		
		//=======================Only for ComparisonCosine
		//OWLClass obsolete = factory.getOWLClass(IRI.create("http://www.geneontology.org/formats/oboInOwl#ObsoleteClass"));
		/*OWLAnnotationProperty deprecated = factory.getOWLAnnotationProperty(IRI.create("http://www.w3.org/2002/07/owl#deprecated"));
		Set<OWLAnnotation> annX = x.getAnnotations(o, deprecated);
		if (annX.iterator().next().isDeprecatedIRIAnnotation())
			System.out.println("Deprecated");*/
		//if (setX.contains(obsolete) || setY.contains(obsolete))
		//	return 0;
		//=======================END
		
		
		double profLCS = prof(lcs);
		
		double dxa = dist(x, lcs);
		double dxroot = profLCS + dxa;//dist(x, root);
		double dya = dist(y, lcs);
		double dyroot = profLCS + dya;//dist(y, root);
		double num = dxa + dya;
		double den = dxroot + dyroot;
		double dtax = num/den;
		dtax = 1.0 - dtax;
		
		/*System.out.println(lcs +  " " + profLCS);
		System.out.println(dxa + " " + dya);
		System.out.println(x + " " + y + " " + dtax);*/
		return dtax;
	}
	
	
	private HashMap<OWLClass, Integer> conceptProfs = new HashMap<OWLClass,Integer>();
	private int conceptMaxDepth = 0;
	
	private HashMap<OWLObjectProperty, Integer> relationProfs = new HashMap<OWLObjectProperty,Integer>();
	private int relationMaxDepth = 0;
	
	
	private void setDistance(OWLClass c1, OWLClass c2, int d)
	{
		Map<OWLClass, Integer> aux = conceptDistances.get(c1);
		
		if (aux == null)
		{
			aux = new HashMap<OWLClass, Integer>();
			conceptDistances.put(c1, aux);
		}
		aux.put(c2, d);
	}
	
	private int getDistance(OWLClass c1, OWLClass c2)
	{
		Map<OWLClass, Integer> aux = conceptDistances.get(c1);
		
		if (aux == null)
			return -1;
		else
		{
			Integer d = aux.get(c2);
			if (d == null)
				return -1;
			else
				return d;
		}
	}
	
	
	public <T> int dist(T c1, T c2)
	{
		int depth = 0;
		if (c1 instanceof OWLClass)
		{
			int dist = getDistance((OWLClass)c1, (OWLClass)c2);
			if (dist != -1)
				return dist;
			Set<OWLClassExpression> c = new HashSet<OWLClassExpression>();
			c.add((OWLClass) c1);
			while (!c.contains(c2) && !c.isEmpty())
			{
				Set<OWLClassExpression> superClasses = new HashSet<OWLClassExpression>();
				for (Iterator<OWLClassExpression> i = c.iterator(); i.hasNext();)
				{
					OWLClassExpression aux = i.next();
					if (!aux.isAnonymous())
					{
						OWLClass cl = aux.asOWLClass();
						superClasses.addAll(cl.getSuperClasses(o));
					}
				}
				c = superClasses;
				depth++;				
			}
			setDistance((OWLClass)c1, (OWLClass)c2, depth);
		}
		if (c1 instanceof OWLObjectProperty)
		{
			Set<OWLObjectPropertyExpression> c = new HashSet<OWLObjectPropertyExpression>();
			c.add((OWLObjectPropertyExpression) c1);
			while (!c.contains(c2) && !c.isEmpty())
			{
				Set<OWLObjectPropertyExpression> superObjectProperties = new HashSet<OWLObjectPropertyExpression>();
				for (Iterator<OWLObjectPropertyExpression> i = c.iterator(); i.hasNext();)
				{
					OWLObjectPropertyExpression aux = i.next();
					if (!aux.isAnonymous())
						superObjectProperties.addAll(aux.getSuperProperties(o));
				}
				c = superObjectProperties;
				depth++;				
			}
		}
		return depth;
	}
	
	public <T> int prof(T _class)
	{
		int depth = 0;
		if (_class instanceof OWLClass)
		{
			//if (conceptProfs.get(_class) != null)
			//	return conceptProfs.get(_class);
			
			depth = dist (_class, factory.getOWLThing());
			//conceptProfs.put((OWLClass) _class, depth);
		}
		if (_class instanceof OWLObjectProperty)
		{
			if (relationProfs.get(_class) != null)
				return relationProfs.get(_class);
			depth = dist (_class, factory.getOWLTopObjectProperty());
			relationProfs.put((OWLObjectProperty) _class, depth);
		}
		return depth;
	}
	
	public int getMaxDepth()
	{
		int max = 0;
		for (Iterator<String> i = concepts.keySet().iterator(); i.hasNext();)
		{
			OWLClass c = getOWLClass(i.next());
			int depth = prof(c);
			if (depth > max)
				max = depth;
		}
		return max;
	}
	
	public String getLabel(OWLConcept c)
	{
		OWLClass x = c.getOWLClass();
		OWLAnnotationProperty label = factory.getOWLAnnotationProperty(IRI.create("http://www.w3.org/2000/01/rdf-schema#label"));
		Set<OWLAnnotation> annX = x.getAnnotations(o, label);
		OWLAnnotation a = annX.iterator().next(); 
		OWLLiteral lit = (OWLLiteral) a.getValue();
		String v = lit.getLiteral();
		return v;
	}
	

	/*public List<OWLAxiom> Fpos_regA (String clA, String prop, String clB)
	{
		OWLClass F = factory.getOWLClass(IRI.create(clA));
		OWLClass A = factory.getOWLClass(IRI.create(clB));
		OWLObjectProperty pos_reg = factory.getOWLObjectProperty(IRI.create(prop));
		OWLObjectSomeValuesFrom somValues = factory.getOWLObjectSomeValuesFrom(pos_reg, A);
		OWLAxiom ax = factory.getOWLSubClassOfAxiom(F, somValues);
		ExplanationProgressMonitor explMonitor = new SilentExplanationProgressMonitor();
    	DefaultExplanationGenerator expl = new DefaultExplanationGenerator(o.getOWLOntologyManager(), new OWLReasonerFactory(), o, reasoner, explMonitor);
    	Set<Set<OWLAxiom>> expAxioms = expl.getExplanations(ax);
    	return new ArrayList<OWLAxiom>(expAxioms.iterator().next());
	}*/
	
	
	public static void main(String[] args)
	{
		
		Map<String, String> ontPrefix = new HashMap<String,String>();
		ontPrefix.put("src/main/resources/dataset3/", "http://purl.org/obo/owl/GO#");
		ontPrefix.put("src/main/resources/dataset32014/", "http://purl.obolibrary.org/obo/");
		String prefix = "src/main/resources/dataset3/";
		String ontFile = prefix + "goProtein/go.owl";
		MyOWLOntology o = new MyOWLOntology(ontFile, ontPrefix.get(prefix));//"http://purl.obolibrary.org/obo/");
		
		
		OWLConcept a = o.getOWLConcept(ontPrefix.get(prefix) + "GO_0055114");
		OWLConcept b = o.getOWLConcept(ontPrefix.get(prefix) + "GO_0030259");
		System.out.println(b.similarity(a));
		
		OWLRelation r1 = o.getOWLRelation("http://purl.org/obo/owl/obo#positively_regulates");
		OWLRelation r2 = o.getOWLRelation("http://purl.org/obo/owl/obo#regulates");
		System.out.println(r1.similarity(r2));
		
	
		
	}
}
