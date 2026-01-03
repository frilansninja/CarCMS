package se.meltastudio.cms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String registrationNumber; // Registreringsnummer

    @Column(name = "last_known_service")
    private Integer lastKnownService;

    private Integer mileage;

    @ManyToOne
    @JoinColumn(name = "vehicle_model_id", nullable = false)
    @JsonBackReference(value = "vehicleModelReference")
    private VehicleModel vehicleModel;
    @ManyToOne
    @JoinColumn(name = "engine_type_id")
    @JsonBackReference(value = "engineTypeReference")
    private EngineType engineType;

    private String transmission;


    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "workorderReference")
    private List<WorkOrder> workOrders = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "workplace_id")
    @JsonBackReference(value = "workshopReference")
    private Workplace workplace;
    @ManyToOne
    @JoinColumn(name = "end_customer_id", nullable = false)
    @JsonBackReference(value = "endCustomerReference")
    private EndCustomer endCustomer;



    @ManyToOne
    @JoinColumn(name ="company_id")
    @JsonBackReference(value = "company-vehicles")
    private Company company;

    @Column(name ="last_known_service_date")
    private LocalDate lastKnownServiceDate;

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public VehicleModel getVehicleModel() {
        return vehicleModel;
    }

    public Integer getLastKnownService() {
        return lastKnownService;
    }

    public void setLastKnownService(Integer lastKnownService) {
        this.lastKnownService = lastKnownService;
    }
    public LocalDate getLastKnownServiceDate() {
        return lastKnownServiceDate;
    }

    public void setLastKnownServiceDate(LocalDate lastKnownServiceDate) {
        this.lastKnownServiceDate = lastKnownServiceDate;
    }

    public void setVehicleModel(VehicleModel vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public void setEngineType(EngineType engineType) {
        this.engineType = engineType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }


    public Workplace getWorkplace() {
        return workplace;
    }

    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }


    public EndCustomer getEndCustomer() {
        return endCustomer;
    }

    public void setEndCustomer(EndCustomer endCustomer) {
        this.endCustomer = endCustomer;
    }

    public List<WorkOrder> getWorkOrders() {
        return workOrders;
    }

    public void setWorkOrders(List<WorkOrder> workOrders) {
        this.workOrders = workOrders;
    }


}
