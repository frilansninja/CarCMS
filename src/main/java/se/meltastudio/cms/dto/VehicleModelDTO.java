package se.meltastudio.cms.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.OneToMany;
import se.meltastudio.cms.model.carservice.VehicleService;

import java.util.List;

public class VehicleModelDTO {

    private Long id;

    private String brand;
    private String model;
    private int year;
    private String fuelType;

    private String engineCode;
    private String transmission;

    private List<Long> vehicleServiceIds;


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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getEngineCode() {
        return engineCode;
    }

    public void setEngineCode(String engineCode) {
        this.engineCode = engineCode;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public List<Long> getVehicleServiceIds() {
        return vehicleServiceIds;
    }

    public void setVehicleServiceIds(List<Long> vehicleServiceIds) {
        this.vehicleServiceIds = vehicleServiceIds;
    }
}
