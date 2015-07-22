package similarity;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ontologyManagement.MyOWLOntology;
import ontologyManagement.OWLConcept;
import test.ComparisonResult;
import test.DatasetTest;

public class InformationContent {
	private static InformationContent instance = null;
	public static InformationContent getInstance() throws Exception
	{
		if (instance == null)
		{
			throw new Exception("No InformationContent instance created. You should call the constructor before calling this method.");
		}
		return instance;
	}
	
	private Map<OWLConcept, Integer> occurrences;
	private Map<OWLConcept, Double> ics;
	private int totalAnnotations;
	
	public InformationContent(List<ComparisonResult> comparisons, String[] files, MyOWLOntology o)
	{
		instance = this;
		ics = new HashMap<OWLConcept, Double>();
		occurrences = new HashMap<OWLConcept, Integer>();
		Set<String> proteins = new HashSet<String>();
		for (Iterator<ComparisonResult> i = comparisons.iterator(); i.hasNext();)
		{
			ComparisonResult cR = i.next();
			proteins.add(cR.getConceptA());
			proteins.add(cR.getConceptB());
		}
		totalAnnotations = 0;
		for (Iterator<String> i = proteins.iterator(); i.hasNext();)
		{
			for (String file:files)
			{
				Set<OWLConcept> anns = DatasetTest.getConceptAnnotations(i.next(), file, o);
				totalAnnotations += anns.size();
				for (Iterator<OWLConcept> j = anns.iterator(); j.hasNext();)
				{
					OWLConcept c = j.next();
					Integer v = occurrences.get(c);
					if (v == null)
						v = 0;
					occurrences.put(c, v + 1);
				}
			}
		}
		for (Iterator<OWLConcept> i = occurrences.keySet().iterator(); i.hasNext();)
		{
			OWLConcept c = i.next();
			this.setIC(c);
		}
	}
	
	public InformationContent(Set<String> entities, String file, MyOWLOntology o)
	{
		instance = this;
		ics = new HashMap<OWLConcept, Double>();
		occurrences = new HashMap<OWLConcept, Integer>();

		totalAnnotations = 0;
		for (Iterator<String> i = entities.iterator(); i.hasNext();)
		{
				Set<OWLConcept> anns = DatasetTest.getConceptAnnotations(i.next(), file, o);
				totalAnnotations += anns.size();
				for (Iterator<OWLConcept> j = anns.iterator(); j.hasNext();)
				{
					OWLConcept c = j.next();
					Integer v = occurrences.get(c);
					if (v == null)
						v = 0;
					occurrences.put(c, v + 1);
				}
		}
		for (Iterator<OWLConcept> i = occurrences.keySet().iterator(); i.hasNext();)
		{
			OWLConcept c = i.next();
			this.setIC(c);
		}
		
	}
	
	protected void setIC(OWLConcept c)
	{
		double freq = 0;
		Set<OWLConcept> subConcepts = c.getSubConcepts();
		subConcepts.add(c);
		for (Iterator<OWLConcept> i = subConcepts.iterator(); i.hasNext();)
		{
			OWLConcept a = i.next();
			Integer aux = occurrences.get(a);
			if (aux == null)
				aux = 0;
			freq += aux;
		}
		ics.put(c, -Math.log(freq/totalAnnotations));
	}
	
	public double getIC(OWLConcept c)
	{
		Double ic = ics.get(c); 
		if ( ic == null)
			return 0;			//Maybe the LCA is not present in the dataset
		double maxIC = Collections.max(ics.values());
		return ics.get(c)/maxIC;
	}

}
