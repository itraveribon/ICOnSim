package test;

import java.util.Map;
import java.util.Set;

import ontologyManagement.MyOWLOntology;
import ontologyManagement.OWLConcept;
import similarity.BipartiteGraphMatching;
import similarity.ComparableElement;

public class Target implements ComparableElement{
	String target;
	Map<String, Double> comparisons;
	String[] files;
	MyOWLOntology o;
	
	public Target(String s, Map<String,Double> c, String[] f, MyOWLOntology onto)
	{
		target = s;
		comparisons = c;
		files = f;
		o = onto;
	}

	public double similarity(ComparableElement a, OWLConcept org, OWLConcept des)
			throws Exception {
		if (a instanceof Target)
			return similarity((Target)a, org, des);
		return 0;
	}
	
	public double similarity (Target c, OWLConcept org, OWLConcept des)
	{
		Double res = comparisons.get(c.target);
		if (res == null)
		{
			res = c.comparisons.get(target);
			if (res == null)
			{
				Set<OWLConcept> a = Dataset4.getGOAnnotations(target, files, o);
				Set<OWLConcept> b = Dataset4.getGOAnnotations(c.target, files, o);
				BipartiteGraphMatching bpm = new BipartiteGraphMatching();
				try {
					res = bpm.matching(a, b, org, des);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		comparisons.put(c.target, res);
		c.comparisons.put(target, res);
		return res;
	}
	
	
}
