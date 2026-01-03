package se.meltastudio.cms.model.carservice;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import se.meltastudio.cms.model.Vehicle;
import se.meltastudio.cms.model.VehicleModel;

@Entity
public class VehicleService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_model_id", nullable = false)
    @JsonBackReference
    private VehicleModel vehicleModel;

    @ManyToOne
    @JoinColumn(name = "service_type_id")
    private VehicleServiceType serviceType;

    @ManyToOne
    @JoinColumn(name = "variation_id", nullable = true)
    private ServiceVariation variation;

    private int startIntervalKm;

    private int startIntervalTimeMonths;



    private int intervalKm;
    private int intervalTimeMonths;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleModel getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(VehicleModel vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public VehicleServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(VehicleServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public ServiceVariation getVariation() {
        return variation;
    }

    public void setVariation(ServiceVariation variation) {
        this.variation = variation;
    }

    public int getStartIntervalKm() {
        return startIntervalKm;
    }

    public void setStartIntervalKm(int startIntervalKm) {
        this.startIntervalKm = startIntervalKm;
    }

    public int getStartIntervalTimeMonths() {
        return startIntervalTimeMonths;
    }

    public void setStartIntervalTimeMonths(int startIntervalTimeMonths) {
        this.startIntervalTimeMonths = startIntervalTimeMonths;
    }

    public int getIntervalTimeMonths() {
        return intervalTimeMonths;
    }

    public void setIntervalTimeMonths(int intervalTimeMonths) {
        this.intervalTimeMonths = intervalTimeMonths;
    }

    public int getIntervalKm() {
        return intervalKm;
    }

    public void setIntervalKm(int intervalKm) {
        this.intervalKm = intervalKm;
    }


    public String getServiceName() {
        return (variation != null) ? variation.getVariation() : serviceType.getServiceName();
    }
}
