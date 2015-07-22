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
import java.util.List;
import java.util.Map;

public class MatrixToRow {

	public static void main(String[] args) {
		InputStream    fis;
		BufferedReader br;
		String         line;
		
		//String file = "src/main/resources/dataset4/commonTargetsSimilarity";
		String file = "/home/traverso/workspace/ExplanationSimilarity146/ordenAnnSim";
		
		List<String> targets = new ArrayList<String>();
		Map<String, Map<String, Double>> targetsSimilarity = new HashMap<String,Map<String, Double>>();
		
		try {
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			while ((line = br.readLine()) != null) {
				targets.add(line);
			}
			br.close();
			
			//file = "/home/traverso/Downloads/DATABASE_Journal_dataset/dataset4/target-target-matrix/GOTargetsCommonSimilarityMat";//seqTargetsCommonSimilarityMat";
			//file = "correlationSeqSim.txt";
			file = "/home/traverso/workspace/ExplanationSimilarity146/annsim_target_sim_matrix.txt";
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			PrintWriter generalWriter = new PrintWriter("AnnSimRow" + ".txt", "UTF-8");
			for (int i = 0; i < targets.size(); i++)
			{
				String[] row = br.readLine().split("\t");//(" "); 
				for (int j = i; j < targets.size(); j++)
				{
					Double seqSim = Double.parseDouble(row[j]);
					generalWriter.println(targets.get(i) + "\t" + targets.get(j) + "\t" + seqSim);
				}
			}
			generalWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
