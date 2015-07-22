package ontologyManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

import similarity.BipartiteGraphMatching;
import similarity.ComparableElement;
import similarity.InformationContent;

public class OWLConcept implements ComparableElement{
	private String uri;
	private MyOWLOntology o;
	private Set<OWLLink> neighbors;
	private boolean satisfiable;
	private OWLClass cl;
	private Map<OWLConcept, Double> ownSimilarities;
	private static Map<OWLConcept,Map<OWLConcept, Double>> similarities = new HashMap<OWLConcept,Map<OWLConcept, Double>>();
	
	public OWLConcept(OWLClass a, MyOWLOntology onto)
	{
		o = onto;
		uri = a.getIRI().toURI().toString();
		neighbors = null;
		satisfiable = isSatisfiable();
		cl = null;
		ownSimilarities = new HashMap<OWLConcept,Double>();
		similarities.put(this, ownSimilarities);
	}
	public OWLClass getOWLClass()
	{
		if (cl == null)
			cl = o.getOWLClass(uri);
		return cl;
	}
	
	public void setNeighbors(Set<OWLLink> n)
	{
		neighbors = n;
	}
	
	public Set<OWLLink> getNeighbors()
	{
		return neighbors;
	}
	
	public void dispose()
	{
		neighbors.clear();
	}
	
	public String getURI()
	{
		return uri;
	}
	
	public String toString()
	{
		return uri;
	}
	
	public String getName()
	{
		return uri.replaceAll("http://purl.obolibrary.org/obo/","").replace("_",":");//("http://purl.org/obo/owl/GO#", "").replace("_", ":");
	}
	
	public boolean isSatisfiable ()
	{
		return o.isSatisfiable(getOWLClass());
	}
	
	private double similarityNeighbors(OWLConcept c)
	{
		BipartiteGraphMatching bpm = new BipartiteGraphMatching();
		if (neighbors == null)
			neighbors = o.getConceptOWLLink(this);
		if (c.neighbors == null)
			c.neighbors = o.getConceptOWLLink(c);
		try {
			double sim = bpm.matching(neighbors, c.neighbors, this, c);
			return sim;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;
	}
	
	public double taxonomicSimilarity(OWLConcept c)
	{
		return o.taxonomicClassSimilarity(getOWLClass(), c.getOWLClass());
	}
	
	
	public Set<OWLConcept> getSubConcepts()
	{
		return o.getSubConcepts(this);
	}
	
	public double similarity(OWLConcept c)
	{
		if (!satisfiable || !c.satisfiable)
			return 0;
		if (this == c)
			return 1.0;
		Double sim = ownSimilarities.get(c);
		if (sim != null)
			return sim;
		double informC = 0;
		try {
			OWLConcept lca = this.getLCA(c);
			InformationContent ic = InformationContent.getInstance();
			informC = ic.getIC(lca);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double taxSim = taxonomicSimilarity(c);
		double neighSim = 1;
		if (taxSim > 0 && informC > 0)
			neighSim = similarityNeighbors(c);

		sim = taxSim*neighSim;
		sim = sim*informC + 0.0;
		
		ownSimilarities.put(c, sim);
		c.ownSimilarities.put(this, sim);
		return sim;
	}

	public OWLConcept getLCA(OWLConcept b)
	{
		return o.getLCS(this, b);
	}
	public double similarity(ComparableElement a, OWLConcept org, OWLConcept des) throws Exception {
		if (!(a instanceof OWLConcept))
			throw new Exception("Invalid comparison between " + this + " and " + a);
		return similarity((OWLConcept)a);
	}

}
