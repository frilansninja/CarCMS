package se.meltastudio.cms.integration.carinfo.dto;

import java.time.LocalDate;

public class CarInfoVehicleDto {

    private String registrationNumber;
    private String vin;

    private String make;
    private String model;
    private String variant;

    private Integer modelYear;
    private String fuelType;
    private String transmission;

    private Integer enginePowerKw;
    private Integer enginePowerHp;

    private Integer curbWeightKg;
    private LocalDate firstRegistrationDate;

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }

    public Integer getModelYear() { return modelYear; }
    public void setModelYear(Integer modelYear) { this.modelYear = modelYear; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public Integer getEnginePowerKw() { return enginePowerKw; }
    public void setEnginePowerKw(Integer enginePowerKw) { this.enginePowerKw = enginePowerKw; }

    public Integer getEnginePowerHp() { return enginePowerHp; }
    public void setEnginePowerHp(Integer enginePowerHp) { this.enginePowerHp = enginePowerHp; }

    public Integer getCurbWeightKg() { return curbWeightKg; }
    public void setCurbWeightKg(Integer curbWeightKg) { this.curbWeightKg = curbWeightKg; }

    public LocalDate getFirstRegistrationDate() { return firstRegistrationDate; }
    public void setFirstRegistrationDate(LocalDate firstRegistrationDate) { this.firstRegistrationDate = firstRegistrationDate; }
}
