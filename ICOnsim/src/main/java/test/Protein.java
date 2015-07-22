package test;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import ontologyManagement.OWLConcept;


@Entity
@Table
public class Protein {
	@Id
    @GeneratedValue
    private Long id;
 
     
    private String name;
     
    @OneToMany(mappedBy="annotations",cascade=CascadeType.PERSIST)
    private List<String> annotations = new ArrayList<String>();
    
    public Protein(String name) {
        this.name = name;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<String> getEmployees() {
        return annotations;
    }
    public void setEmployees(List<String> annotations) {
        this.annotations = annotations;
    }
}
