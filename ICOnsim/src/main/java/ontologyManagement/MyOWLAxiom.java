package ontologyManagement;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

import similarity.ComparableElement;




public abstract class MyOWLAxiom implements ComparableElement {
	
	//private OWLAxiom axiom;
	protected MyOWLOntology o;
	
	protected MyOWLAxiom(OWLAxiom b , MyOWLOntology onto) {
		//axiom = b;
		o = onto;
	}

	public abstract double similarity(MyOWLAxiom baxiom, OWLConcept origin, OWLConcept destiny) throws Exception;
	/*public double similarity(MyOWLAxiom baxiom, OWLConcept origin, OWLConcept destiny) throws Exception {
		OWLAxiom a = axiom;
		OWLAxiom b = baxiom.axiom;
		if (a.getAxiomType() != b.getAxiomType())
			return 0.0;
		
		if (a instanceof OWLTransitiveObjectPropertyAxiom)
		{
			OWLObjectProperty pa = (OWLObjectProperty) ((OWLTransitiveObjectPropertyAxiom) a).getProperty();
			OWLObjectProperty pb = (OWLObjectProperty) ((OWLTransitiveObjectPropertyAxiom) b).getProperty();
			return o.taxonomicPropertySimilarity(pa, pb);
		}
		if (a instanceof OWLSubClassOfAxiom)
		{
			OWLClassExpression subA = ((OWLSubClassOfAxiom) a).getSubClass();
			OWLClassExpression superA = ((OWLSubClassOfAxiom) a).getSuperClass();
			OWLClassExpression subB = ((OWLSubClassOfAxiom) b).getSubClass();
			OWLClassExpression superB = ((OWLSubClassOfAxiom) b).getSuperClass();
			double simSub = 0;
			double simSuper = 0;
			//If the classes are the compared originally, makes not sense to penalize the similarity again and again
			simSub = similarity(subA, subB, origin, destiny);
			simSuper = similarity(superA, superB, origin, destiny);
				
			return (simSub + simSuper)/2;
		}
		if (a instanceof OWLObjectSomeValuesFrom)
		{
			return similarity((OWLClassExpression)a,(OWLClassExpression)b, origin, destiny);
		}
		else
		{
			throw new Exception("We do not know how to deal with axioms as \n" + this + "\n" + a);
		}
	}
	
	
	private double similarity (OWLObjectPropertyExpression a, OWLObjectPropertyExpression b, OWLConcept origin, OWLConcept destiny) throws Exception
	{
		if (!a.isAnonymous() && !b.isAnonymous())
		{
			return o.taxonomicPropertySimilarity(a.asOWLObjectProperty(), b.asOWLObjectProperty());
		}
		else
		{
			throw new Exception("We do not know how to deal with the properties \n" + a + "\n" + b);
		}
	}
	
	private double similarity (OWLClassExpression a, OWLClassExpression b, OWLConcept origin, OWLConcept destiny) throws Exception
	{
		if (a.getClassExpressionType() != b.getClassExpressionType())
			return 0.0;
		switch (a.getClassExpressionType())
		{
			case OBJECT_SOME_VALUES_FROM:
			{
				OWLObjectProperty ra = (OWLObjectProperty) ((OWLObjectSomeValuesFrom) a).getProperty();
				OWLClassExpression fa = ((OWLObjectSomeValuesFrom) a).getFiller();
				OWLObjectProperty rb = (OWLObjectProperty) ((OWLObjectSomeValuesFrom) b).getProperty();
				OWLClassExpression fb = ((OWLObjectSomeValuesFrom) b).getFiller();
				return (o.taxonomicPropertySimilarity(ra, rb) + similarity(fa, fb, origin, destiny))/2;
			}
			case OWL_CLASS:
				if (a.asOWLClass() == origin.getOWLClass() && b.asOWLClass() == destiny.getOWLClass() || a.asOWLClass() == destiny.getOWLClass() && b.asOWLClass() == origin.getOWLClass())
					return 1;
				return o.taxonomicClassSimilarity(a.asOWLClass(), b.asOWLClass());
			default:
				throw new Exception("We do not know how to deal with the OWLClassExpresions \n" + a + "\n" + b);
		}
	}*/

	public double similarity(ComparableElement a, OWLConcept org, OWLConcept des) throws Exception {
		if (!(a instanceof MyOWLAxiom))
			throw new Exception("Invalid comparison");
		return similarity((MyOWLAxiom)a, org, des);
	}
	
	public abstract String toString();

}
