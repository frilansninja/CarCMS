package se.meltastudio.cms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "part_mapping")
public class PartMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_model_id", nullable = false)
    private VehicleModel vehicleModel;

    @ManyToOne
    @JoinColumn(name = "task_template_id", nullable = false)
    private WorkTaskTemplate workTaskTemplate;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    public PartMapping() {}

    public PartMapping(VehicleModel vehicleModel, WorkTaskTemplate workTaskTemplate, Article article) {
        this.vehicleModel = vehicleModel;
        this.workTaskTemplate = workTaskTemplate;
        this.article = article;
    }

    public Long getId() { return id; }

    public VehicleModel getVehicleModel() { return vehicleModel; }

    public WorkTaskTemplate getWorkTaskTemplate() { return workTaskTemplate; }

    public Article getArticle() { return article; }

    public void setVehicleModel(VehicleModel vehicleModel) { this.vehicleModel = vehicleModel; }

    public void setWorkTaskTemplate(WorkTaskTemplate workTaskTemplate) { this.workTaskTemplate = workTaskTemplate; }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer quantity;

    public void setArticle(Article article) { this.article = article; }
}
