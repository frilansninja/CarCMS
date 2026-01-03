package se.meltastudio.cms.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="work_order_category")
public class WorkOrderCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<WorkTaskTemplate> taskTemplates;

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

    public List<WorkTaskTemplate> getTaskTemplates() {
        return taskTemplates;
    }

    public void setTaskTemplates(List<WorkTaskTemplate> taskTemplates) {
        this.taskTemplates = taskTemplates;
    }


}

