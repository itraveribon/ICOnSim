package similarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Configuration.BlockingStrategyType;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.reasoner.knowledgeexploration.OWLKnowledgeExplorerReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;


import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.ExplanationProgressMonitor;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;



public class ExplanationPrinter {

	/*public static String getLabel(String iri)
	{
		IRI E_Iri = IRI.create(iri);
		OWLClass E = factory.getOWLClass(E_Iri);
        Set<OWLAnnotation> anns = E.getAnnotations(o, factory.getRDFSLabel());
        for (Iterator<OWLAnnotation> i = anns.iterator(); i.hasNext();)
        {
        	OWLAnnotation a = i.next();
        	System.out.println(((OWLLiteral) a.getValue()).getLiteral());
        }
	}*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String ontFile = "src/main/resources/dataset3/goProtein/goCurrent1.owl";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		
		/*try {
			OWLOntology o = manager.loadOntologyFromOntologyDocument(new File(ontFile));
			OWLDataFactory factory = manager.getOWLDataFactory();

	        // These two lines are the only relevant difference between this code and the original example
	        // This example uses HermiT: http://hermit-reasoner.com/
			// create the Pellet reasoner
			OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
			Configuration configuration = new Configuration();
			OWLReasoner reasoner =  reasonerFactory.createReasoner(o, configuration);
			
			ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
	        // Create the DLQueryPrinter helper class. This will manage the
	        // parsing of input and printing of results
			//=================================================
			DLQueryEngine engine = new DLQueryEngine(reasoner, shortFormProvider);
			DLQueryParser parser = new DLQueryParser(reasoner.getRootOntology(), shortFormProvider);
			//=================================================
	        DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner,
	                shortFormProvider), shortFormProvider);
	        ExplanationProgressMonitor explMonitor = new SilentExplanationProgressMonitor();
	        DefaultExplanationGenerator expl = new DefaultExplanationGenerator(manager, new PelletReasonerFactory(), o, reasoner, explMonitor);
	        
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
	        while (true) {
	            System.out
	                    .println("Type a class expression in Manchester Syntax and press Enter (or press x to exit):");
	            String classExpression = br.readLine();
	            OWLClassExpression superClass = parser.parseClassExpression(classExpression);
	            // Check for exit condition
	            if (classExpression == null || classExpression.equalsIgnoreCase("x")) {
	                break;
	            }
	            dlQueryPrinter.askQuery(classExpression);
	            System.out.println();
	            OWLClass cl = engine.getSubClasses(classExpression, true).iterator().next();
	            
	            OWLAxiom axiom = new OWLSubClassOfAxiomImpl(cl, superClass, new HashSet<OWLAnnotation>());
	            Set<Set<OWLAxiom>> explanations = expl.getExplanations(axiom); 
	            for (Iterator<Set<OWLAxiom>> i = explanations.iterator(); i.hasNext();)
	            {
	            	System.out.println(i.next());
	            }
	            System.out.println();
	            IRI E_Iri = IRI.create("http://www.semanticweb.org/traverso/ontologies/2014/10/untitled-ontology-281#C");
	            OWLClass E = factory.getOWLClass(E_Iri);
	            Set<OWLClass> superClasses ;//= E.getSuperClasses(o);
	            NodeSet<OWLClass> n = reasoner.getSuperClasses(E, false);
	            superClasses = n.getFlattened();
	            for (Iterator<OWLClass> i = superClasses.iterator(); i.hasNext();)
	            {
	            	System.out.println(i.next());
	            }
	        }*/
			try{	
				OWLOntology o = manager.loadOntologyFromOntologyDocument(new File(ontFile));
				OWLDataFactory factory = manager.getOWLDataFactory();
	            OWLReasonerFactory reasonerFactory = null;
	          
	            reasonerFactory = new Reasoner.ReasonerFactory();//new FaCTPlusPlusReasonerFactory(); //new StructuralReasonerFactory();
	            Configuration configuration = new Configuration();
                configuration.ignoreUnsupportedDatatypes = true;
                configuration.blockingStrategyType = BlockingStrategyType.OPTIMAL;
                
	            OWLReasoner reasoner =  reasonerFactory.createReasoner(o,configuration);
	            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
	            reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
	            reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_ASSERTIONS);
	            reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
	            
	            IRI E_Iri = IRI.create("http://purl.obolibrary.org/obo/GO_0045726");
	            OWLClass E = factory.getOWLClass(E_Iri);
	            Set<OWLAnnotation> anns = E.getAnnotations(o, factory.getRDFSLabel());
	            String labelE = "'" + ((OWLLiteral) anns.iterator().next().getValue()).getLiteral() + "'";
	            
	            IRI A_Iri = IRI.create("http://purl.obolibrary.org/obo/GO_0007009");
	            OWLClass A = factory.getOWLClass(A_Iri);
	            anns = A.getAnnotations(o, factory.getRDFSLabel());
	            String labelA = "'" + ((OWLLiteral) anns.iterator().next().getValue()).getLiteral() + "'";
	            
	            OWLObjectProperty pos_reg = factory.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002213"));
	            OWLObjectSomeValuesFrom pos_regA = factory.getOWLObjectSomeValuesFrom(pos_reg, A);
	            anns = pos_reg.getAnnotations(o, factory.getRDFSLabel());
	            String labelPosReg = "'" + ((OWLLiteral) anns.iterator().next().getValue()).getLiteral() + "'";
	            
	            ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		        // Create the DLQueryPrinter helper class. This will manage the
		        // parsing of input and printing of results
				//=================================================
				DLQueryEngine engine = new DLQueryEngine(reasoner, shortFormProvider);
				DLQueryParser parser = new DLQueryParser(reasoner.getRootOntology(), shortFormProvider);
				//=================================================
		        DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner, shortFormProvider), shortFormProvider);
		        String classExpression = "GO_0045726" + " and " + "RO_0002213" + " some " + "GO_0007009";
		        System.out.println(classExpression);
		       // dlQueryPrinter.askQuery(classExpression);
		        //OWLClassExpression superClass = parser.parseClassExpression(classExpression);
	            Set<OWLClass> equivalents = engine.getEquivalentClasses(classExpression);//.getEquivalentClasses(classExpression);
	            System.out.println(equivalents);
	            Set<OWLClassExpression> s1 = new HashSet<OWLClassExpression>();
	            s1.add(E);
	            s1.add(pos_regA);
	            OWLObjectIntersectionOf iof = factory.getOWLObjectIntersectionOf(s1);
	            
	            OWLEquivalentClassesAxiom Epos_regA = factory.getOWLEquivalentClassesAxiom(E, iof);
	            //OWLSubClassOfAxiom Epos_regA = factory.getOWLSubClassOfAxiom(E, pos_regA);
	            
	            System.out.println(Epos_regA + " " + reasoner.isEntailed(Epos_regA));
	            ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory);
	            ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(o);
	            Set<Explanation<OWLAxiom>> expl = gen.getExplanations(Epos_regA, 2);
	            System.out.println(expl.iterator().next().getAxioms());
	          
	            /*ExplanationProgressMonitor explMonitor = new SilentExplanationProgressMonitor();
		        DefaultExplanationGenerator expl = new DefaultExplanationGenerator(manager, reasonerFactory, o, reasoner, explMonitor);
		        Set<Set<OWLAxiom>> explanations = expl.getExplanations(Epos_regA,1); 
	            for (Iterator<Set<OWLAxiom>> i = explanations.iterator(); i.hasNext();)
	            {
	            	System.out.println(i.next());
	            }*/
	            
	            //System.out.println(reasoner.getEquivalentClasses(Epos_regA));
	           
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}/* catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/ catch (OWLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
