package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ontologyManagement.MyOWLOntology;
import ontologyManagement.OWLConcept;
import similarity.BipartiteGraphMatching;

public class ComparisonCosine {
	public static void main (String[] args) throws Exception
	{
		//String ontFile = "src/main/resources/dataset3/goProtein/go_200808-termdbOriginal.owl";//goCurrent1.owl";
		String ontFile = "src/main/resources/dataset32014/goProtein/go.owl";//goCurrent1.owl";
		MyOWLOntology o = new MyOWLOntology(ontFile);
		//String comparisonFile = "src/main/resources/dataset3/proteinpairs.txt";
		String comparisonFile = "src/main/resources/dataset32014/proteinpairs.txt";
		List<ComparisonResult> comparisons = DatasetTest.readComparisonFile(comparisonFile);
		PrintWriter generalWriter = new PrintWriter("resultsOnSim2014.txt", "UTF-8");
		int counter = 0, total = comparisons.size(), memoryControl = 0;
		//String[] files = {"src/main/resources/dataset3/cellular_annt", "src/main/resources/dataset3/molecularFunction_annt", "src/main/resources/dataset3/process_annt"};
		//String[] files = {"src/main/resources/dataset3/process_annt"};
		//String[] files = {"src/main/resources/dataset32014/process_annt"};
		String[] files = {"src/main/resources/dataset32014/process_annt"};
		/*Map<String, PrintWriter> writers = new HashMap<String, PrintWriter>();
		for (int i = 0; i < files.length; i++)
		{
			writers.put(files[i], new PrintWriter("results" + i +".txt", "UTF-8"));
		}*/
		Set<OWLConcept> annotations = new HashSet<OWLConcept>();
		for (Iterator<ComparisonResult> i = comparisons.iterator(); i.hasNext();)
		{
			ComparisonResult comp = i.next();
			for (String file:files)
			{
				annotations.addAll(DatasetTest.getConceptAnnotations(comp.getConceptA(), file, o));
				annotations.addAll(DatasetTest.getConceptAnnotations(comp.getConceptB(), file, o));
			}
		}
		/*Map<OWLConcept,Map<OWLConcept, Double>> results = new HashMap<OWLConcept, Map<OWLConcept, Double>>();
		for (Iterator<OWLConcept> i = annotations.iterator(); i.hasNext();)
		{
			results.put(i.next(), new HashMap<OWLConcept, Double>());
		}*/
		Set<ComparisonResult> results = new HashSet<ComparisonResult>();
		//============================ Fill file
		String f = "CESSM2014/process/resultsCosineOnSim2014.txt";
		FileInputStream fis = new FileInputStream(f);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
		String line; 
		while ((line = br.readLine()) != null)
		{
			String[] aux = line.split("\t");
			String c1 = aux[0];
			String c2 = aux[1];
			double sim = Double.parseDouble(aux[2]);
			ComparisonResult cR = new ComparisonResult(c1, c2);
			cR.setSimilarity(sim);
			results.add(cR);
			
		}
		br.close();
		//=================================
		
		Map<String, OWLConcept> mapAnn = new HashMap<String, OWLConcept>();
		for (Iterator<OWLConcept> i = annotations.iterator(); i.hasNext();)
		{
			OWLConcept c = i.next();
			for (Iterator<OWLConcept> j = annotations.iterator(); j.hasNext();)
			{
				OWLConcept d = j.next();
				//System.out.println(c.getName() + " " + " " + d.getName());
				mapAnn.put(c.getName(), c);
				mapAnn.put(d.getName(), d);
				results.add(new ComparisonResult(c.getName(), d.getName()));
			}
		}
		total = results.size();
		counter = 1;
		for (Iterator<ComparisonResult> i = results.iterator(); i.hasNext();)
		{
			ComparisonResult c = i.next();
			if (c.getSimilarity() < 0)
				c.setSimilarity(mapAnn.get(c.getConceptA()).similarity(mapAnn.get(c.getConceptB())));
			System.out.println(c + "\t" + counter++ + "/" + total);
			generalWriter.println(c);
		}
		generalWriter.close();
		
	}
}
