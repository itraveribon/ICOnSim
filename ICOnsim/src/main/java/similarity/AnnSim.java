package similarity;

import java.util.Collections;
import java.util.Set;
import ontologyManagement.OWLConcept;
import ontologyManagement.OWLLink;

public class AnnSim {
	ComparableElement[] v1, v2;
	double[][] costMatrix;
	int[] assignment;
	//Map<ComparableElement,ComparableElement> map;
	
	public AnnSim()
	{
		//map = new HashMap<ComparableElement,ComparableElement>();
	}
		
	public <T> double matching(Set<T> a, Set<T> b, OWLConcept orig, OWLConcept des) throws Exception
	{
		if (a.getClass() != b.getClass() && a != Collections.emptySet() && b != Collections.emptySet())// || !(a instanceof Set<ComparableElement>)))// || !(a instanceof Set<ComparableElement>))
			throw new Exception("Invalid comparison between " + a.getClass() + " " + b.getClass());
		else
		{
			if (a.equals(b))
				return 1.0;
			if (a.isEmpty() || b.isEmpty()) //Here we know that, almost one of the set is not empty
				return 0.0;
			costMatrix = new double [a.size()][b.size()];
			v1 = a.toArray(new ComparableElement[a.size()]);
			v2 = b.toArray(new ComparableElement[b.size()]);
			for (int i = 0; i< v1.length; i++)
			{
				ComparableElement s1 = v1[i];
				for (int j = 0; j < v2.length; j++)
				{
					ComparableElement s2 = v2[j];
					costMatrix[i][j] = 1 - s1.similarity(s2,orig,des); //The hungarian algorithm minimize. Therefore we convert the similarity in distance
					//System.out.println(s1.toString().replaceAll("http://purl.obolibrary.org/obo/", "") + "\t" + s2.toString().replaceAll("http://purl.obolibrary.org/obo/", "") + "\t" + (1-costMatrix[i][j]));
				}
			}
			HungarianAlgorithm hungarn = new HungarianAlgorithm(costMatrix);
			assignment = hungarn.execute();
			
			double sim = 0;
			for (int i = 0; i < assignment.length; i++)
			{
				int aux = assignment[i];
				//System.out.println(v1[i]);
				if (aux >=0) //If there is an assignment
				{
					//System.out.println(v2[aux]);
					//map.put(v1[i], v2[aux]);
					double print = 1-costMatrix[i][aux];
					
					//System.out.println(((OWLConcept) v1[i]).getName() + "\t" + ((OWLConcept) v2[aux]).getName() + "\t" + print);
					/*if (v1[i] instanceof OWLLink)
					{
						System.out.println(((OWLLink) v1[i]).getExplanations());
						System.out.println();
						System.out.println(((OWLLink) v2[aux]).getExplanations());
					}*/
					sim += 1-costMatrix[i][aux];
				}
			}
			/*int dum = 0;
			for (int i = 0; i < v1.length; i++)
			{
				int aux = assignment[i];
				if (aux >=0) //If there is an assignment
				{
					System.out.println(((OWLConcept) v1[i]).getName() + "-1" + "\t" + ((OWLConcept) v2[aux]).getName() + "-2");
				}
				else
				{
					System.out.println(((OWLConcept) v1[i]).getName() + "-1" + "\t" + "DUMMY" + dum);
					dum++;
				}
			}*/
			return 2*sim/(v1.length+v2.length);
		}
	}
	
	/*public Map<ComparableElement, ComparableElement> getAssignment()
	{
		return map;
	}*/
}

