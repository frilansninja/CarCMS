package se.meltastudio.cms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class WorkTaskTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference(value = "category-templates")
    private WorkOrderCategory category;

    @ManyToOne
    @JoinColumn(name = "repair_category_id")
    @JsonBackReference(value = "repaircategory-templates")
    private RepairCategory repairCategory;

    @OneToMany(mappedBy = "taskTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Förhindrar rekursion vid serialisering
    @JsonManagedReference(value = "requiredPartsReference")
    private List<RequiredPartTemplate> requiredParts;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer estimatedTime;

    // Getters och Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public WorkOrderCategory getCategory() { return category; }
    public void setCategory(WorkOrderCategory category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public RepairCategory getRepairCategory() { return repairCategory; }
    public void setRepairCategory(RepairCategory repairCategory) { this.repairCategory = repairCategory; }
    public Integer getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(Integer estimatedTime) { this.estimatedTime = estimatedTime; }
    public List<RequiredPartTemplate> getRequiredParts() { return requiredParts; }
    public void setRequiredParts(List<RequiredPartTemplate> requiredParts) { this.requiredParts = requiredParts; }

}
