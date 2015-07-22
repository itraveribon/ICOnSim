package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import similarity.BipartiteGraphMatching;

public class Dataset4 {
	public static Set<String> getGenes (String drug, String folder)
	{
		Set<String> annotations = new HashSet<String>();
		String file = folder + "/" + drug;
		
		InputStream    fis;
		BufferedReader br;
		String         line;
		
		try {
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			line = br.readLine(); //First line contains the number of annotations
			int numAnnotations = Integer.parseInt(line);
			for (int i = 0; i < numAnnotations; i++){
				line = br.readLine();
			    annotations.add(line);
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
	
	public static Set<OWLConcept> getGOAnnotations (Set<String> genes, String[] files, MyOWLOntology o)
	{
		Set<OWLConcept> annotations = new HashSet<OWLConcept>();
		for (Iterator<String> i = genes.iterator(); i.hasNext();)
		{
			String g = i.next();
			annotations.addAll(getGOAnnotations(g, files, o));
		}
		return annotations;
	}
	
	public static Set<OWLConcept> getGOAnnotations (String g, String[] files, MyOWLOntology o)
	{
		Set<OWLConcept> annotations = new HashSet<OWLConcept>();
		for (String f: files)
		{
			Set<OWLConcept> a = DatasetTest.getConceptAnnotations(g, f, o);
			annotations.addAll(a);
		}
		return annotations;
	}
	
	public static void main(String[] args)
	{
		//readFiles();
		String ontFile = "src/main/resources/dataset3/goProtein/go_200808-termdb.owl";//goCurrent1.owl";
		MyOWLOntology o = new MyOWLOntology(ontFile);
		String file = "src/main/resources/dataset4/commonDrugsSimilarity";
		InputStream    fis;
		BufferedReader br;
		String         line;
		Set<ComparisonResult> comparisons = new HashSet<ComparisonResult>();
		String[] files = {"src/main/resources/dataset4/BiologicalProcess"};
		//Map<String, PrintWriter> writers = new HashMap<String, PrintWriter>();
		
		
		try {
			PrintWriter generalWriter = new PrintWriter("resultsDataset4" + ".txt", "UTF-8");
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			br.readLine();
			Set<String> targets = new HashSet<String>();
			while ((line = br.readLine()) != null) {
				targets.add(line);
			}
			br.close();
			for (Iterator<String> i = targets.iterator(); i.hasNext();)
			{
				String s = i.next();
				for (Iterator<String> j = targets.iterator(); j.hasNext();)
				{
					String z = j.next();
					comparisons.add(new ComparisonResult(s,z));
				}
			}
			// ===================== END GET COMPARISONS ====================================
			
			int counter = 0;
			int total = comparisons.size();
			String drugAnn = "src/main/resources/dataset4/drugs_t_annts";
			for (Iterator<ComparisonResult> i = comparisons.iterator(); i.hasNext();)
			{
				ComparisonResult comp = i.next();
				String t1 = comp.getConceptA();
				String t2 = comp.getConceptB();
				Set<OWLConcept> a = getGOAnnotations(getGenes(t1, drugAnn), files, o);
				Set<OWLConcept> b = getGOAnnotations(getGenes(t2, drugAnn), files, o);
				BipartiteGraphMatching bpm = new BipartiteGraphMatching();
				double sim = bpm.matching(a, b, null, null); 
				comp.setSimilarity(sim);
				System.out.println(comp + "\t" + counter++ + "/" + total);// + " " + owlConceptComparisons.size());
				generalWriter.println(comp);
				
			}
			generalWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void readFilesTargets()
	{
		String file = "/home/traverso/Downloads/DATABASE_Journal_dataset/dataset4/test";
		InputStream    fis;
		BufferedReader br;
		String         line;
		try {
			fis = new FileInputStream(file);
			
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			br.readLine();
			while ((line = br.readLine()) != null) {
			    String[] elements = line.replaceAll(" ","").split("\t");
			    String targetName = elements[0].replaceAll("hsa:", "");
			    String biological = elements[1];
			    String[] annotations = biological.split(";");
			    PrintWriter generalWriter = new PrintWriter("src/main/resources/dataset4/BiologicalProcess/" + targetName, "UTF-8");
			    generalWriter.println(annotations.length);
			    for (int i = 0; i < annotations.length; i++)
			    {
			    	generalWriter.println(annotations[i]);
			    }
			    generalWriter.close();
			    String cellular = elements[2];
			    annotations = cellular.split(";");
			    generalWriter = new PrintWriter("src/main/resources/dataset4/CellularComponent/" + targetName, "UTF-8");
			    generalWriter.println(annotations.length);
			    for (int i = 0; i < annotations.length; i++)
			    {
			    	generalWriter.println(annotations[i]);
			    }
			    generalWriter.close();
			    String molecular = elements[3];
			    annotations = molecular.split(";");
			    generalWriter = new PrintWriter("src/main/resources/dataset4/MolecularFunction/" + targetName, "UTF-8");
			    generalWriter.println(annotations.length);
			    for (int i = 0; i < annotations.length; i++)
			    {
			    	generalWriter.println(annotations[i]);
			    }
			    generalWriter.close();
			}

			// Done with the file
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
