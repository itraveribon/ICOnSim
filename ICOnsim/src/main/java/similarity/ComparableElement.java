package similarity;

import ontologyManagement.OWLConcept;

public interface ComparableElement {
	double similarity(ComparableElement a, OWLConcept org, OWLConcept des) throws Exception;
}
