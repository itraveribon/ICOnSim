package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ontologyManagement.MyOWLOntology;
import similarity.BipartiteGraphMatching;

public class Dataset4Drug {

	public static void main(String[] args) {
		String file = "Dataset4Target/resultsDataset4Targets.txt";
		InputStream    fis;
		BufferedReader br;
		String         line;
		Map<String, Map<String, Double>> targetsSimilarity = new HashMap<String,Map<String, Double>>();
		String[] files = {"src/main/resources/dataset4/BiologicalProcess"};
		String ontFile = "src/main/resources/dataset3/goProtein/goCurrent1.owl";
		MyOWLOntology o = new MyOWLOntology(ontFile);
		
		try {
			PrintWriter generalWriter = new PrintWriter("resultsDataset4" + ".txt", "UTF-8");
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			while ((line = br.readLine()) != null) {
				String[] aux = line.split("\t");
				Map<String, Double> targetComp = targetsSimilarity.get(aux[0]);
				if (targetComp == null)
				{
					targetComp = new HashMap<String, Double>();
					targetsSimilarity.put(aux[0], targetComp);
				}
				targetComp.put(aux[1], Double.parseDouble(aux[2]));
			}
			br.close();
			Map<String, Target> targets = new HashMap<String, Target>();
			for (Iterator<String> i = targetsSimilarity.keySet().iterator(); i.hasNext();)
			{
				String t = i.next();
				targets.put(t, new Target(t, targetsSimilarity.get(t), files, o));
			}
			file = "src/main/resources/dataset4/commonDrugsSimilarity";
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			Set<String> drugs = new HashSet<String>();
			while ((line = br.readLine()) != null) {
				drugs.add(line);
			}
			br.close();
			Set<ComparisonResult> comparisons = new HashSet<ComparisonResult>();
			for (Iterator<String> i = drugs.iterator(); i.hasNext();)
			{
				String s = i.next();
				for (Iterator<String> j = drugs.iterator(); j.hasNext();)
				{
					String z = j.next();
					comparisons.add(new ComparisonResult(s,z));
				}
			}
			int counter = 0;
			int total = comparisons.size();
			String drugAnn = "src/main/resources/dataset4/drugs_t_annts";
			for (Iterator<ComparisonResult> i = comparisons.iterator(); i.hasNext();)
			{
				ComparisonResult comp = i.next();
				String t1 = comp.getConceptA();
				String t2 = comp.getConceptB();
				Set<String> targetsStringT1 = Dataset4.getGenes(t1, drugAnn);
				Set<String> targetsStringT2 = Dataset4.getGenes(t2, drugAnn);
				Set<Target> targetsT1 = new HashSet<Target>();
				Set<Target> targetsT2 = new HashSet<Target>();
				for (Iterator<String> j = targetsStringT1.iterator(); j.hasNext();)
				{
					String aux = j.next();
					Target t = targets.get(aux);
					if (t == null)
						t = new Target(aux, new HashMap<String, Double>(), files, o);
					targetsT1.add(t);
				}
				for (Iterator<String> j = targetsStringT2.iterator(); j.hasNext();)
				{
					String aux = j.next();
					Target t = targets.get(aux);
					if (t == null)
						t = new Target(aux, new HashMap<String, Double>(), files, o);
					targetsT2.add(t);
				}
				BipartiteGraphMatching bpm = new BipartiteGraphMatching();
				double sim = bpm.matching(targetsT1, targetsT2, null, null); 
				comp.setSimilarity(sim);
				System.out.println(comp + "\t" + counter++ + "/" + total);// + " " + owlConceptComparisons.size());
				generalWriter.println(comp);
			}
			generalWriter.close();
		} catch (FileNotFoundException e) {
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

}
