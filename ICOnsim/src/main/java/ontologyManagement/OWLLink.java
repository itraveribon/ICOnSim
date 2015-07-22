package ontologyManagement;

import java.util.Set;

import similarity.BipartiteGraphMatching;
import similarity.ComparableElement;





public class OWLLink implements ComparableElement {
	OWLRelation relation;
	OWLConcept destiny;
	Set<OWLExplanation> explanations;
	

	public OWLLink( OWLRelation r, OWLConcept b, Set<OWLExplanation> exp) {
		relation = r;
		destiny = b;
		explanations = exp;
	}
	
	public Set<OWLExplanation> getExplanations() {
		return explanations;
	}

	public void setExplanations(Set<OWLExplanation> explanations) {
		this.explanations = explanations;
	}
	
	public String toString()
	{
		return relation.toString() + " " + destiny.toString();
	}
	
	
	public double similarity(OWLLink a, OWLConcept conceptA, OWLConcept conceptB)
	{
		BipartiteGraphMatching bpm = new BipartiteGraphMatching();
		
		try {
			double sim = 0;
			/*double simTaxRel = relation.similarity(a.relation);
			if (simTaxRel == 0)
				sim = 0;
			else
			{
				double simTaxDes = destiny.taxonomicSimilarity(a.destiny);
				double simExp = bpm.matching(explanations, a.explanations, conceptA, conceptB);
				sim = 0.1*simTaxRel + 0.5*simTaxDes + 0.4*simExp;
			}*/
			double simTaxRel = relation.similarity(a.relation);
			double simTaxDes = destiny.taxonomicSimilarity(a.destiny);
			double simExp = 0;
			if (simTaxRel != 0 && simTaxDes != 0)
				simExp = bpm.matching(explanations, a.explanations, conceptA, conceptB);
			sim = simTaxRel*simTaxDes*simExp;//Math.min(simTaxRel*simTaxDes, simExp);
			return sim;
			//return (simTaxRel + simTaxDes)/2;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;
	}

	public double similarity(ComparableElement a, OWLConcept org, OWLConcept des) throws Exception {
		if (!(a instanceof OWLLink))
			throw new Exception("Invalid comparison");
		return similarity((OWLLink)a, org, des);
	}
}
