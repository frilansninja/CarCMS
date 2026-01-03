package se.meltastudio.cms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;


import java.util.List;

@Entity
public class WorkTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    @JsonBackReference
    private WorkOrder workOrder;

    @Column(nullable = false)
    private String description;

    public List<WorkTaskArticle> getWorkTaskArticles() {
        return workTaskArticles;
    }

    @OneToMany(mappedBy = "workTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkTaskArticle> workTaskArticles;

    public WorkTaskTemplate getWorkTaskTemplate() {
        return workTaskTemplate;
    }

    public void setWorkTaskTemplate(WorkTaskTemplate workTaskTemplate) {
        this.workTaskTemplate = workTaskTemplate;
    }

    @ManyToOne
    @JoinColumn(name = "work_task_template_id")
    private WorkTaskTemplate workTaskTemplate;


    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private WorkOrderStatus workOrderStatus;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkOrderStatus getWorkOrderStatus() {
        return workOrderStatus;
    }

    public void setWorkOrderStatus(WorkOrderStatus status) {
        this.workOrderStatus = status;
    }


    public void setWorkTaskArticles(List<WorkTaskArticle> workTaskArticles) {
        this.workTaskArticles = workTaskArticles;
    }
}
