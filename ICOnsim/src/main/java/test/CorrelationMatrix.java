package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CorrelationMatrix {

	public static void main(String[] args) {
		String file = "src/main/resources/dataset4/commonTargetsSimilarity";
		InputStream    fis;
		BufferedReader br;
		String         line;
		
		List<String> targets = new ArrayList<String>();
		Map<String, Map<String, Double>> targetsSimilarity = new HashMap<String,Map<String, Double>>();
		
		try {
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			while ((line = br.readLine()) != null) {
				targets.add(line);
			}
			br.close();
			
			file = "resultsDataset4Targets.txt";
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
			
			/*file = "/home/traverso/Downloads/DATABASE_Journal_dataset/dataset4/target-target-matrix/seqTargetsCommonSimilarityMat";
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));*/
			PrintWriter generalWriter = new PrintWriter("correlationSeqSim" + ".txt", "UTF-8");
			for (int i = 0; i < targets.size(); i++)
			{
				//String[] row = br.readLine().split(" "); 
				for (int j = 0; j < targets.size(); j++)
				{
					//Double seqSim = Double.parseDouble(row[j]);
					Double sim = targetsSimilarity.get(targets.get(i)).get(targets.get(j));
					if (sim == null)
					{
						String aux = targets.get(j);
						System.out.println(aux);
						sim = targetsSimilarity.get(aux).get(targets.get(i));
					}
					generalWriter.print(sim + " ");
				}
				generalWriter.print("\n");
			}
			generalWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
