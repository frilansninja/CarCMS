package se.meltastudio.cms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class RepairCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "repairCategory")
    @JsonIgnore // Förhindrar cirkulär referens
    private List<WorkTaskTemplate> templates;

    // Getters och Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<WorkTaskTemplate> getTemplates() { return templates; }
    public void setTemplates(List<WorkTaskTemplate> templates) { this.templates = templates; }
}
