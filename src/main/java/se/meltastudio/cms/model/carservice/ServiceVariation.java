package se.meltastudio.cms.model.carservice;

import jakarta.persistence.*;

@Entity
public class ServiceVariation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public VehicleServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(VehicleServiceType serviceType) {
        this.serviceType = serviceType;
    }

    private String variation;

    @ManyToOne
    @JoinColumn(name = "service_type_id")
    private VehicleServiceType serviceType;
}
