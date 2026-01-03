package se.meltastudio.cms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Workplace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // Namn på verkstaden

    // Adressinformation
    @Column
    private String address;  // Gatuadress

    @Column
    private String city;  // Stad/ort

    @Column
    private String zipCode;  // Postnummer

    @Column
    private String country;  // Land

    @Column
    private String phone;  // Telefon till verkstaden

    @Column
    private String email;  // E-post till verkstaden

    // I Workplace:
    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonBackReference(value = "company-workplaces")
    private Company company;

    @OneToMany(mappedBy = "workplace", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "workshopReference")
    private List<Vehicle> vehicles = new ArrayList<>();



    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
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
}
