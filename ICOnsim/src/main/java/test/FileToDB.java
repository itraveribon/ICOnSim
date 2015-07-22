package test;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import ontologyManagement.OWLConcept;

public class FileToDB {
	public static void main (String[] args) throws Exception
	{
		String[] files = {"src/main/resources/dataset3/cellular_annt", "src/main/resources/dataset3/molecularFunction_annt", "src/main/resources/dataset3/process_annt"};
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		  
        session.beginTransaction();
        
		for (String file:files)
		{
			File f = new File(file);
			File[] proteins = f.listFiles();
			for (File p: proteins)
			{
				String prote = p.toString().replace(".*" + file, "");
			}
			Set<OWLConcept> a = getConceptAnnotations(comp.getConceptA(), file, o);
			Set<OWLConcept> b = getConceptAnnotations(comp.getConceptB(), file, o);
		}
	}
}
