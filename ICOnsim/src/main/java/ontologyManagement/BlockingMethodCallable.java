package ontologyManagement;

import java.util.concurrent.Callable;

import org.semanticweb.owlapi.model.OWLAxiom;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;

public class BlockingMethodCallable implements Callable {
	OWLAxiom axiom;
	int max;
	DefaultExplanationGenerator expl;
	public BlockingMethodCallable(DefaultExplanationGenerator expGen, int maxExp)
	{
		axiom = null;
		max = maxExp;
		expl = expGen;
	}
	public Object call() throws Exception {
		return expl.getExplanations(axiom, max);
	}
	
	public void setAxiom(OWLAxiom ax)
	{
		axiom = ax;
	}

}
