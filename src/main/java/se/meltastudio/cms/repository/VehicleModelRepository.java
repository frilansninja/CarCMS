package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.meltastudio.cms.model.Vehicle;
import se.meltastudio.cms.model.VehicleModel;

import java.util.List;
import java.util.Optional;

public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {


    Optional<VehicleModel> findByBrandAndModelAndYear(String brand, String model, int year);
}
