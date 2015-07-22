# ICOnSim
Extension of the similarity measure OnSim with Information Content. A Similarity Measure for Determining Relatedness Between Ontology Terms.

[1] Ignacio Traverso-Ribón, Maria-Esther Vidal and Guillermo Palma. A Similarity Measure for Determining Relatedness Between Ontology Terms. 11th  International Conference on Data Integration in the Life Sciences 2015 (DILS2015).

[2] Ignacio Traverso-Ribón, Maria-Esther Vidal. Exploiting Information Content and Semantics to Accurately Computing Similarity of GO-based Annotated Entities. 2015 IEEE Conference on Computational Intelligence in Bioinformatics and Computational Biology

[3] Palma, G.; Vidal, M. E.; Haag, E.; Raschid, L.; Thor, A. Measuring Relatedness Between Scientific Entities in Annotation Datasets. ACM International Conference on Bioinformatics, Computational Biology, and Biomedical Informatics (BCB), 2013


# Setting up AnnSim-OnSim
Once you have downloaded the code from GitHub you only need to modify the file src/main/java/test/DatasetTest.java to run AnnSim-OnSim.

IC-OnSim need as inputs:
* An OWL Ontology.
* A file describing the comparisons to be performed.
* A file for each entity containing their annotations.

## Comparison file
The comparison file follows the following format:

    ENTITY1 ENTITY2
    ENTITY1 ENTITY3
    ENTITY3 ENTITY4

Each line represents a pair of entities to be compared. These entities can be, e.g., proteins. In this case, the file will look like this:

    Q9JKJ9	Q9NYL5
    Q9JKJ9	Q60991
    Q9JKJ9	Q64505
    Q9JKJ9	P11511
    Q9JKJ9	P27786

## Annotation files
For each entity present in the comparison file, AnnSim-OnSim needs a file containing its annotations. The annotations are terms from the given ontology. In case our entities are proteins annotated with the Gene Ontology, the file would look like this:

    3
    GO:0045454
    GO:0006467
    GO:0055114

The first line contains the number of annotations included in the file and each annotations is written in a different line.

## Main example

    Map<String, String> ontPrefix = new HashMap<String,String>();
		ontPrefix.put("src/main/resources/dataset3/", "http://purl.org/obo/owl/GO#");
		String prefix = "src/main/resources/dataset3/";
		String ontFile = prefix + "goProtein/go.owl"; // File containing the OWL Ontology
		
		MyOWLOntology o = new MyOWLOntology(ontFile, ontPrefix.get(prefix));
		String comparisonFile = "src/main/resources/dataset3/proteinpairs.txt"; // File containing the entity pairs to be compared.

		List<ComparisonResult> comparisons = readComparisonFile(comparisonFile);
		int counter = 0, total = comparisons.size();

		String[] files = {"src/main/resources/dataset3/process_annt"}; //Folder containing the annotations files
		
		for (Iterator<ComparisonResult> i = comparisons.iterator(); i.hasNext();)
		{
			ComparisonResult comp = i.next();
			double sim = 0;
			for (String file:files)
			{
				Set<OWLConcept> a = getConceptAnnotations(comp.getConceptA(), file, o);
				Set<OWLConcept> b = getConceptAnnotations(comp.getConceptB(), file, o);
				AnnSim bpm = new AnnSim();
				double aux = bpm.matching(a, b, null, null); 
				String s = comp.getConceptA() + "\t" + comp.getConceptB() + "\t" + aux;
				sim += aux;
			}
			comp.setSimilarity(sim/files.length);
			System.out.println(comp + "\t" + counter++ + "/" + total);
		}
		

# License
This work is licensed under [GNU/GPL v2](https://www.gnu.org/licenses/gpl-2.0.html).
