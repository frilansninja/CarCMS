package se.meltastudio.cms.dto;

import se.meltastudio.cms.model.carservice.VehicleServiceType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderDTO {

    private Long id;
    private String description; // Beskrivning av jobbet
    private LocalDate createdDate; // Datum då ordern skapades

    private Long mechanicId;
    private String mechanicName;
    private WorkOrderStatusDTO workOrderStatus;
    private List<Long> partOrderIds = new ArrayList<>();
    private Long vehicleId;

    public VehicleDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleDTO vehicle) {
        this.vehicle = vehicle;
    }

    private VehicleDTO vehicle;
    private Long categoryId;

    public List<VehicleServiceType> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<VehicleServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    private List<VehicleServiceType> serviceTypes = new ArrayList<>();

    public List<WorkTaskDTO> getWorkTasks() {
        return workTasks;
    }

    public void setWorkTasks(List<WorkTaskDTO> workTasks) {
        this.workTasks = workTasks;
    }

    private List<WorkTaskDTO> workTasks = new ArrayList<>();

    public WorkOrderDTO() {
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

    public Long getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(Long mechanicId) {
        this.mechanicId = mechanicId;
    }

    public WorkOrderStatusDTO getWorkOrderStatus() { return workOrderStatus; }

    public void setWorkOrderStatus(WorkOrderStatusDTO workOrderStatusId) {
        this.workOrderStatus = workOrderStatus;
    }

    public List<Long> getPartOrderIds() {
        return partOrderIds;
    }

    public void setPartOrderIds(List<Long> partOrderIds) {
        this.partOrderIds = partOrderIds;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getMechanicName() {
        return mechanicName;
    }

    public void setMechanicName(String mechanicName) {
        this.mechanicName = mechanicName;
    }
}
