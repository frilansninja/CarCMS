package se.meltastudio.cms.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import se.meltastudio.cms.enums.WorkOrderType;


@Entity
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description; // Beskrivning av jobbet
    private LocalDate createdDate; // Datum då ordern skapades

    public User getMechanic() {
        return mechanic;
    }

    public void setMechanic(User mechanic) {
        this.mechanic = mechanic;
    }

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    @JsonBackReference(value = "status-workorders")
    private WorkOrderStatus status;

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PartOrder> partOrders = new ArrayList<>();


    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WorkTask> workTasks = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "mechanic_id")
    @JsonBackReference(value = "mechanic-workorders")

    private User mechanic;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonBackReference(value = "workorderReference")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference(value = "category-workorders")
    private WorkOrderCategory category;


    public WorkOrderCategory getCategory() {
        return category;
    }

    public void setCategory(WorkOrderCategory category) {
        this.category = category;
    }

    public List<WorkTask> getWorkTasks() {
        return workTasks;
    }

    public void setWorkTasks(List<WorkTask> workTasks) {
        this.workTasks = workTasks;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<PartOrder> getPartOrders() {
        return partOrders;
    }

    public void setPartOrders(List<PartOrder> partOrders) {
        this.partOrders = partOrders;
    }

}
