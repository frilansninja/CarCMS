package se.meltastudio.cms.dto;

import java.util.List;

public class CompanyDTO {
    private Long id;
    private String name;
    private String orgNumber;
    private String phone;
    private String email;
    private String address;

    // Billing information
    private String bankgiro;
    private String plusgiro;
    private String vatNumber;
    private Integer paymentTerms;
    private String gln;
    private String billingStreet;
    private String billingCity;
    private String billingZip;
    private String billingCountry;

    // Company settings
    private Integer customerInactiveDays; // Days before customer is considered inactive
    private Integer customerGdprDeletionDays; // Days after which inactive customers are anonymized for GDPR

    // Exempel: i stället för att returnera hela entiteter av t.ex. Workplace,
    // kan vi välja att bara returnera en lista med dess ID:n
    private List<Long> workplaceIds;
    private List<Long> endCustomerIds;
    private List<Long> userIds;
    private List<Long> vehicleIds;

    // --- Getters / Setters (eller använd Lombok om du vill) ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id = id; }

    public String getName() {
        return name;
    }
    public void setName(String name) { this.name = name; }

    public String getOrgNumber() {
        return orgNumber;
    }
    public void setOrgNumber(String orgNumber) { this.orgNumber = orgNumber; }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) { this.address = address; }

    public List<Long> getWorkplaceIds() {
        return workplaceIds;
    }
    public void setWorkplaceIds(List<Long> workplaceIds) {
        this.workplaceIds = workplaceIds;
    }

    public List<Long> getEndCustomerIds() {
        return endCustomerIds;
    }
    public void setEndCustomerIds(List<Long> endCustomerIds) {
        this.endCustomerIds = endCustomerIds;
    }

    public List<Long> getUserIds() {
        return userIds;
    }
    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<Long> getVehicleIds() {
        return vehicleIds;
    }
    public void setVehicleIds(List<Long> vehicleIds) {
        this.vehicleIds = vehicleIds;
    }

    public String getBankgiro() {
        return bankgiro;
    }
    public void setBankgiro(String bankgiro) {
        this.bankgiro = bankgiro;
    }

    public String getPlusgiro() {
        return plusgiro;
    }
    public void setPlusgiro(String plusgiro) {
        this.plusgiro = plusgiro;
    }

    public String getVatNumber() {
        return vatNumber;
    }
    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public Integer getPaymentTerms() {
        return paymentTerms;
    }
    public void setPaymentTerms(Integer paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getGln() {
        return gln;
    }
    public void setGln(String gln) {
        this.gln = gln;
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

    public Integer getCustomerInactiveDays() {
        return customerInactiveDays;
    }
    public void setCustomerInactiveDays(Integer customerInactiveDays) {
        this.customerInactiveDays = customerInactiveDays;
    }

    public Integer getCustomerGdprDeletionDays() {
        return customerGdprDeletionDays;
    }
    public void setCustomerGdprDeletionDays(Integer customerGdprDeletionDays) {
        this.customerGdprDeletionDays = customerGdprDeletionDays;
    }
}
