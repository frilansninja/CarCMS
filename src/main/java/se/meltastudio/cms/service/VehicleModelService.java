package se.meltastudio.cms.service;

import org.springframework.stereotype.Service;
import se.meltastudio.cms.model.VehicleModel;
import se.meltastudio.cms.model.carservice.VehicleService;
import se.meltastudio.cms.repository.VehicleModelRepository;
import se.meltastudio.cms.repository.VehicleServiceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleModelService {

    private final VehicleModelRepository vehicleModelRepository;
    private final VehicleServiceRepository vehicleServiceRepository;

    public VehicleModelService(VehicleModelRepository vehicleModelRepository, VehicleServiceRepository vehicleServiceRepository) {
        this.vehicleModelRepository = vehicleModelRepository;
        this.vehicleServiceRepository = vehicleServiceRepository;
    }

    public Optional<VehicleModel> getVehicleModel(String brand, String model, int year) {
        return vehicleModelRepository.findByBrandAndModelAndYear(brand, model, year);
    }

    public List<VehicleService> getServiceIntervalsForModel(String brand, String model, int year) {
        return vehicleModelRepository.findByBrandAndModelAndYear(brand, model, year)
                .map(vehicleServiceRepository::findByVehicleModel)
                .orElseThrow(() -> new RuntimeException("Bilmodell ej hittad"));
    }

    public VehicleModel addVehicleModel(VehicleModel vehicleModel) {
        return vehicleModelRepository.save(vehicleModel);
    }
}
