package se.meltastudio.cms.parts.api;

/**
 * Vehicle context information for parts search.
 * Contains vehicle identity data used to find compatible parts.
 */
public class VehicleContext {

    private String registrationNumber;
    private String vin;
    private String make;
    private String model;
    private Integer year;
    private String engineCode;
    private String engineDisplacement;

    public VehicleContext() {
    }

    public VehicleContext(String registrationNumber, String vin, String make, String model, Integer year) {
        this.registrationNumber = registrationNumber;
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.year = year;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getEngineCode() {
        return engineCode;
    }

    public void setEngineCode(String engineCode) {
        this.engineCode = engineCode;
    }

    public String getEngineDisplacement() {
        return engineDisplacement;
    }

    public void setEngineDisplacement(String engineDisplacement) {
        this.engineDisplacement = engineDisplacement;
    }
}
