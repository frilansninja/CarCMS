package se.meltastudio.cms.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @Column(nullable = false, unique = true)
    @JsonProperty("name")
    private String name;

    @Column(nullable = false, unique = true)
    @JsonProperty("orgNumber")
    private String orgNumber;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    // Billing information
    @Column
    @JsonProperty("bankgiro")
    private String bankgiro;

    @Column
    @JsonProperty("plusgiro")
    private String plusgiro;

    @Column
    @JsonProperty("vatNumber")
    private String vatNumber; // Momsnummer

    @Column
    @JsonProperty("paymentTerms")
    private Integer paymentTerms; // Betalningsvillkor i dagar (t.ex. 30 dagar)

    @Column
    @JsonProperty("gln")
    private String gln; // GS1 Global Location Number

    @Column
    @JsonProperty("billingStreet")
    private String billingStreet;

    @Column
    @JsonProperty("billingCity")
    private String billingCity;

    @Column
    @JsonProperty("billingZip")
    private String billingZip;

    @Column
    @JsonProperty("billingCountry")
    private String billingCountry;

    // Company settings for customer management
    @Column
    @JsonProperty("customerInactiveDays")
    private Integer customerInactiveDays; // Days of inactivity before customer is considered inactive (null = never)

    @Column
    @JsonProperty("customerGdprDeletionDays")
    private Integer customerGdprDeletionDays; // Days after which inactive customers are anonymized for GDPR (null = never)

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "company-workplaces")
    private List<Workplace> workplaces = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "company-endCustomers")
    private List<EndCustomer> endCustomers = new ArrayList<>();

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @OneToMany(mappedBy = "company")
    @JsonManagedReference
    private List<User> users;


    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "company-vehicles")
    private List<Vehicle> vehicles = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgNumber() {
        return orgNumber;
    }

    public void setOrgNumber(String orgNumber) {
        this.orgNumber = orgNumber;
    }

    public String getPhone() {
        return phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Workplace> getWorkplaces() {
        return workplaces;
    }

    public void setWorkplaces(List<Workplace> workplaces) {
        this.workplaces = workplaces;
    }

    public List<EndCustomer> getEndCustomers() {
        return endCustomers;
    }

    public void setEndCustomers(List<EndCustomer> endCustomers) {
        this.endCustomers = endCustomers;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}
