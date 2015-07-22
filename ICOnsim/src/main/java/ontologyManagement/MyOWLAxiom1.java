package ontologyManagement;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

import similarity.ComparableElement;

public class MyOWLAxiom1 implements ComparableElement{
	private OWLAxiom axiom;
	protected MyOWLOntology o;
	
	public MyOWLAxiom1(OWLAxiom b , MyOWLOntology onto) {
		axiom = b;
		o = onto;
	}

	public double similarity(MyOWLAxiom1 baxiom, OWLConcept origin, OWLConcept destiny) throws Exception
	{
		AxiomType<?> aType = this.getAxiomType();
		if (aType != baxiom.getAxiomType())
			return 0;
		if (aType == AxiomType.SUB_OBJECT_PROPERTY)
		{
			MyOWLSubObjectPropertyOfAxiom ax = new MyOWLSubObjectPropertyOfAxiom(axiom, o);
			MyOWLSubObjectPropertyOfAxiom bx = new MyOWLSubObjectPropertyOfAxiom(baxiom.axiom, o);
			return ax.similarity(bx, origin, destiny);
		}
		if (aType == AxiomType.SUBCLASS_OF)
		{
			MyOWLSubClassOfAxiom ax = new MyOWLSubClassOfAxiom(axiom, o);
			MyOWLSubClassOfAxiom bx = new MyOWLSubClassOfAxiom(baxiom.axiom, o);
			return ax.similarity(bx, origin, destiny);
		}
		if (aType == AxiomType.SUB_PROPERTY_CHAIN_OF)
		{
			MyOWLSubPropertyChainOfAxiom ax = new MyOWLSubPropertyChainOfAxiom(axiom, o);
			MyOWLSubPropertyChainOfAxiom bx = new MyOWLSubPropertyChainOfAxiom(baxiom.axiom, o);
			return ax.similarity(bx, origin, destiny);
		}
		if (aType == AxiomType.TRANSITIVE_OBJECT_PROPERTY)
		{
			MyOWLTransitiveObjectPropertyAxiom ax = new MyOWLTransitiveObjectPropertyAxiom(axiom, o);
			MyOWLTransitiveObjectPropertyAxiom bx = new MyOWLTransitiveObjectPropertyAxiom(baxiom.axiom, o);
			return ax.similarity(bx, origin, destiny);
		}
		if (aType == AxiomType.INVERSE_OBJECT_PROPERTIES)
		{
			MyOWLInverseObjectPropertyAxiom ax = new MyOWLInverseObjectPropertyAxiom(axiom, o);
			MyOWLInverseObjectPropertyAxiom bx = new MyOWLInverseObjectPropertyAxiom(baxiom.axiom, o);
			return ax.similarity(bx, origin, destiny);
		}
		if (axiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES)
		{
			MyOWLEquivalentClassAxiom ax = new MyOWLEquivalentClassAxiom(axiom, o);
			MyOWLEquivalentClassAxiom bx = new MyOWLEquivalentClassAxiom(baxiom.axiom, o);
			return ax.similarity(bx, origin, destiny);
		}
		else
		{
			throw new Exception("We do not know how to deal with axioms as \n" + this + "\n" + aType);
		}
	}
	
	public AxiomType<?> getAxiomType()
	{
		return axiom.getAxiomType();
	}

	public double similarity(ComparableElement a, OWLConcept org, OWLConcept des) throws Exception {
		if (!(a instanceof MyOWLAxiom1))
			throw new Exception("Invalid comparison");
		return similarity((MyOWLAxiom1)a, org, des);
	}
	
	public String toString()
	{
		return axiom.toString();
	}
}
