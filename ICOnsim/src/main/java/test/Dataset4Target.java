package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ontologyManagement.MyOWLOntology;
import ontologyManagement.OWLConcept;
import similarity.BipartiteGraphMatching;

public class Dataset4Target {

	public static void main(String[] args)
	{
		//readFiles();
		String ontFile = "src/main/resources/dataset3/goProtein/goCurrent1.owl";
		MyOWLOntology o = new MyOWLOntology(ontFile);
		String file = "src/main/resources/dataset4/commonTargetsSimilarity";
		InputStream    fis;
		BufferedReader br;
		String         line;
		Set<ComparisonResult> comparisons = new HashSet<ComparisonResult>();
		//String[] files = {"src/main/resources/dataset4/BiologicalProcess"};
		String[] files = {"src/main/resources/dataset4/BiologicalProcess", "src/main/resources/dataset4/CellularComponent", "src/main/resources/dataset4/MolecularFunction"};
		//Map<String, PrintWriter> writers = new HashMap<String, PrintWriter>();
		
		
		try {
			PrintWriter generalWriter = new PrintWriter("resultsDataset4TargetsTest" + ".txt", "UTF-8");
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
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
			//String drugAnn = "src/main/resources/dataset4/drugs_t_annts";
			for (Iterator<ComparisonResult> i = comparisons.iterator(); i.hasNext();)
			{
				ComparisonResult comp = i.next();
				String t1 = comp.getConceptA();
				String t2 = comp.getConceptB();
				Set<OWLConcept> a = Dataset4.getGOAnnotations(t1, files, o);
				Set<OWLConcept> b = Dataset4.getGOAnnotations(t2, files, o);
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

}
