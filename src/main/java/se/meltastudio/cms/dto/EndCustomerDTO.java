package se.meltastudio.cms.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EndCustomerDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;

    private String billingStreet;
    private String billingCity;
    private String billingZip;
    private String billingCountry;

    // ID på det företag (Company) som äger "slutkunden"
    private Long companyId;

    // Om du vill skicka med en lista över fordonens ID
    private List<Long> vehicleIds = new ArrayList<>();

    // Archive fields
    private Boolean isActive;
    private LocalDateTime archivedDate;

    // --- Getters & setters ---

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBillingStreet() {
        return billingStreet;
    }
    public void setBillingStreet(String billingStreet) {
        this.billingStreet = billingStreet;
    }

    public String getBillingCity() {
        return billingCity;
    }
    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBillingZip() {
        return billingZip;
    }
    public void setBillingZip(String billingZip) {
        this.billingZip = billingZip;
    }

    public String getBillingCountry() {
        return billingCountry;
    }
    public void setBillingCountry(String billingCountry) {
        this.billingCountry = billingCountry;
    }

    public Long getCompanyId() {
        return companyId;
    }
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public List<Long> getVehicleIds() {
        return vehicleIds;
    }
    public void setVehicleIds(List<Long> vehicleIds) {
        this.vehicleIds = vehicleIds;
    }

    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getArchivedDate() {
        return archivedDate;
    }
    public void setArchivedDate(LocalDateTime archivedDate) {
        this.archivedDate = archivedDate;
    }
}
