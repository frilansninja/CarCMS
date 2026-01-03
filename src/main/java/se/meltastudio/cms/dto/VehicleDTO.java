package se.meltastudio.cms.dto;

import java.time.LocalDate;
import java.util.List;

public class VehicleDTO {
    private Long id;
    private String brand;  // Mappas från vehicleModel.getBrand()
    private Long vehicleModelId;  // Eventuellt kan det vara samma som brand om du har en separat modell


    private String modelName;
    public List<Long> workOrderIds;

    private LocalDate lastKnownServiceDate;



    private Long workplaceId;
    private String workplaceName;

    private String transmission;
    private Integer lastKnownService;
    private Long endCustomerId;
    private String endCustomerName;
    private Integer mileage;
    private Long companyId;
    private String companyName;
    private String registrationNumber;
    private Integer year;
    private boolean hasActiveWorkOrders;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public void setLastKnownService(Integer lastKnownService) {
        this.lastKnownService = lastKnownService;
    }



    public List<Long> getWorkOrderIds() {
        return workOrderIds;
    }

    public void setWorkOrderIds(List<Long> workOrderIds) {
        this.workOrderIds = workOrderIds;
    }



    public Long getWorkplaceId() {
        return workplaceId;
    }



    public LocalDate getLastKnownServiceDate() {
        return lastKnownServiceDate;
    }

    public void setLastKnownServiceDate(LocalDate lastKnownServiceDate) {
        this.lastKnownServiceDate = lastKnownServiceDate;
    }


    public void setWorkplaceId(Long workplaceId) {
        this.workplaceId = workplaceId;
    }
    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }



    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }


    public Integer getLastKnownService() {
        return lastKnownService;
    }

    public void setLastKnownService(int lastKnownService) {
        this.lastKnownService = lastKnownService;
    }



    public Long getEndCustomerId() {
        return endCustomerId;
    }

    public void setEndCustomerId(Long endCustomerId) {
        this.endCustomerId = endCustomerId;
    }



    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Long getVehicleModelId() {
        return vehicleModelId;
    }

    public void setVehicleModelId(Long vehicleModelId) {
        this.vehicleModelId = vehicleModelId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getWorkplaceName() {
        return workplaceName;
    }

    public void setWorkplaceName(String workplaceName) {
        this.workplaceName = workplaceName;
    }

    public String getEndCustomerName() {
        return endCustomerName;
    }

    public void setEndCustomerName(String endCustomerName) {
        this.endCustomerName = endCustomerName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public boolean isHasActiveWorkOrders() {
        return hasActiveWorkOrders;
    }

    public void setHasActiveWorkOrders(boolean hasActiveWorkOrders) {
        this.hasActiveWorkOrders = hasActiveWorkOrders;
    }

}
