package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.meltastudio.cms.model.VehicleModel;
import se.meltastudio.cms.model.carservice.VehicleService;

import java.util.List;

@Repository
public interface VehicleServiceRepository extends JpaRepository<VehicleService, Long> {
    List<VehicleService> findByVehicleModel(VehicleModel vehicleModel);
}
