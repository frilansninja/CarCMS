package se.meltastudio.cms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EndCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Slutkundens namn
    private String email; // E-post
    private String phone; // Telefonnummer

    // Faktureringsadress
    private String billingStreet;
    private String billingCity;
    private String billingZip;
    private String billingCountry;

    // Archive/Soft delete functionality
    @Column(nullable = false)
    private Boolean isActive = true; // Default to active

    @Column
    private LocalDateTime archivedDate; // When the customer was archived

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    @JsonBackReference(value = "company-endCustomers")
    private Company company;

    @OneToMany(mappedBy = "endCustomer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "endCustomerReference")// Hanterar serialisering av fordon
    private List<Vehicle> vehicles = new ArrayList<>();


    public EndCustomer(String name, String email, String phone, String billingStreet, String billingCity, String billingZip, String billingCountry) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.billingStreet = billingStreet;
        this.billingCity = billingCity;
        this.billingZip = billingZip;
        this.billingCountry = billingCountry;
        this.vehicles = new ArrayList<>();
    }

    public EndCustomer() {}

    public EndCustomer(Long id) {
        this.id = id;
    }


    // 🔹 Getters och Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Company getCompany() { return company; }

    public void setCompany(Company company) { this.company = company; }

    public String getBillingStreet() { return billingStreet; }
    public void setBillingStreet(String billingStreet) { this.billingStreet = billingStreet; }

    public String getBillingCity() { return billingCity; }
    public void setBillingCity(String billingCity) { this.billingCity = billingCity; }

    public String getBillingZip() { return billingZip; }
    public void setBillingZip(String billingZip) { this.billingZip = billingZip; }

    public String getBillingCountry() { return billingCountry; }
    public void setBillingCountry(String billingCountry) { this.billingCountry = billingCountry; }

    public List<Vehicle> getVehicles() { return vehicles; }
    public void setVehicles(List<Vehicle> vehicles) { this.vehicles = vehicles; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getArchivedDate() { return archivedDate; }
    public void setArchivedDate(LocalDateTime archivedDate) { this.archivedDate = archivedDate; }
}
