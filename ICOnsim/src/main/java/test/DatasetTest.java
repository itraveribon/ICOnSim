package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ontologyManagement.MyOWLOntology;
import ontologyManagement.OWLConcept;
import similarity.AnnSim;
import similarity.BipartiteGraphMatching;

public class DatasetTest {
	
	public static Map<OWLConcept, Integer> getComparisonMap(List<ComparisonResult> comparisons, MyOWLOntology o)
	{
		Map<OWLConcept, Integer> owlConceptComparisons = new HashMap<OWLConcept, Integer>();
		//String[] files = {"src/main/resources/dataset3/cellular_annt", "src/main/resources/dataset3/molecularFunction_annt", "src/main/resources/dataset3/process_annt"};
		String[] files = {"src/main/resources/dataset3/process_annt"};
		for (String file:files)
		{
			for (Iterator<ComparisonResult> i = comparisons.iterator(); i.hasNext();)
			{
				ComparisonResult comp = i.next();
				Set<OWLConcept> a = getConceptAnnotations(comp.getConceptA(), file, o);
				Set<OWLConcept> b = getConceptAnnotations(comp.getConceptB(), file, o);
				for (Iterator<OWLConcept> j = a.iterator(); j.hasNext();)
				{
					OWLConcept c = j.next();
					if (owlConceptComparisons.get(c) == null)
					{
						owlConceptComparisons.put(c, 0);
					}
					owlConceptComparisons.put(c, owlConceptComparisons.get(c) + 1);
				}
				for (Iterator<OWLConcept> j = b.iterator(); j.hasNext();)
				{
					OWLConcept c = j.next();
					if (owlConceptComparisons.get(c) == null)
					{
						owlConceptComparisons.put(c, 0);
					}
					owlConceptComparisons.put(c, owlConceptComparisons.get(c) + 1);
				}
			}
		}
		return owlConceptComparisons;
	}
	
	public static void updateComparisonMap(Set<OWLConcept> a, Set<OWLConcept> b, Map<OWLConcept, Integer> owlConceptComparisons)
	{
		for (Iterator<OWLConcept> i = a.iterator(); i.hasNext();)
		{
			OWLConcept c = i.next();
			owlConceptComparisons.put(c, owlConceptComparisons.get(c) - 1);
		}
		for (Iterator<OWLConcept> i = b.iterator(); i.hasNext();)
		{
			OWLConcept c = i.next();
			owlConceptComparisons.put(c, owlConceptComparisons.get(c) - 1);
		}
	}
	
	public static void freeMemory (Map<OWLConcept, Integer> owlConceptComparisons, MyOWLOntology o)
	{
		Set<OWLConcept> removable = new HashSet<OWLConcept>();
		for (Iterator<OWLConcept> i = owlConceptComparisons.keySet().iterator(); i.hasNext();)
		{
			OWLConcept c = i.next();
			if (owlConceptComparisons.get(c) == 0)
			{
				o.removeConcept(c);
				removable.add(c);
			}
		}
		for (Iterator<OWLConcept> i = removable.iterator(); i.hasNext();)
			owlConceptComparisons.remove(i.next());
	}
	
	
	public static void main (String[] args) throws Exception
	{
		Map<String, String> ontPrefix = new HashMap<String,String>();
		ontPrefix.put("src/main/resources/dataset3/", "http://purl.org/obo/owl/GO#");
		String prefix = "src/main/resources/dataset3/";
		String ontFile = prefix + "goProtein/go.owl";
		
		MyOWLOntology o = new MyOWLOntology(ontFile, ontPrefix.get(prefix));
		String comparisonFile = "src/main/resources/dataset3/proteinpairs.txt";

		List<ComparisonResult> comparisons = readComparisonFile(comparisonFile);
		PrintWriter generalWriter = new PrintWriter("results.txt", "UTF-8");
		int counter = 0, total = comparisons.size();

		String[] files = {"src/main/resources/dataset3/process_annt"};
		Map<String, PrintWriter> writers = new HashMap<String, PrintWriter>();
		for (int i = 0; i < files.length; i++)
		{
			writers.put(files[i], new PrintWriter("results" + i +".txt", "UTF-8"));
		}
		for (Iterator<ComparisonResult> i = comparisons.iterator(); i.hasNext();)
		{
			ComparisonResult comp = i.next();
			double sim = 0;
			for (String file:files)
			{
				Set<OWLConcept> a = getConceptAnnotations(comp.getConceptA(), file, o);
				Set<OWLConcept> b = getConceptAnnotations(comp.getConceptB(), file, o);
				AnnSim bpm = new AnnSim();
				double aux = bpm.matching(a, b, null, null); 
				String s = comp.getConceptA() + "\t" + comp.getConceptB() + "\t" + aux;
				writers.get(file).println(s);
				sim += aux;
			}
			comp.setSimilarity(sim/files.length);
			System.out.println(comp + "\t" + counter++ + "/" + total);
			generalWriter.println(comp);
		}
		generalWriter.close();
		for (String file: files)
		{
			writers.get(file).close();
		}
		
	}
	
	public static List<ComparisonResult> readComparisonFile(String comparisonFile)
	{
		List<ComparisonResult> comparisons = new ArrayList<ComparisonResult>();
		
		InputStream    fis;
		BufferedReader br;
		String         line;
		
		try {
			fis = new FileInputStream(comparisonFile);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			while ((line = br.readLine()) != null) {
			    String[] elements = line.split("\t");
			    comparisons.add(new ComparisonResult(elements[0], elements[1]));
			}

			// Done with the file
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return comparisons;
	}
	
	private static Set<File> getFiles(File f)
	{
		File[] files = f.listFiles();
		Set<File> setFile = new HashSet<File>();
		for (int i = 0; i < files.length; i++)
		{
			if (files[i].isDirectory())
			{
				setFile.addAll(getFiles(files[i]));
			}
			else
				setFile.add(files[i]);
		}
		return setFile;
	}
	
	public static Set<OWLConcept> getConceptAnnotations(String conceptName, String folder, MyOWLOntology o)
	{
		File f = new File(folder + "/" + conceptName);
		if (f.exists())
			return getAnnotations(f,o);
		
		return new HashSet<OWLConcept>();
	}
	
	public static Set<OWLConcept> getAnnotations(File f, MyOWLOntology o)
	{
		InputStream    fis;
		BufferedReader br;
		String         line;
		
		Set<OWLConcept> annotations = new HashSet<OWLConcept>();
		
		try {
			fis = new FileInputStream(f);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			line = br.readLine(); //First line contains the number of annotations
			int numAnnotations = Integer.parseInt(line);
			for (int i = 0; i < numAnnotations; i++){
				line = br.readLine();
				line = line.split("\t")[0];
				String element = o.getOntologyPrefix() + line.replace(":", "_");
			    OWLConcept c = o.getOWLConcept(element);
			    if (c != null)
			    	annotations.add(c);
			}

			// Done with the file
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return annotations;
	}

}
