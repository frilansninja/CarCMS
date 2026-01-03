package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.meltastudio.cms.model.PartMapping;
import se.meltastudio.cms.model.VehicleModel;
import se.meltastudio.cms.model.WorkTaskTemplate;

import java.util.Optional;
import java.util.List;

public interface PartMappingRepository extends JpaRepository<PartMapping, Long> {

    Optional<PartMapping> findByVehicleModelAndWorkTaskTemplate(VehicleModel vehicleModel, WorkTaskTemplate workTaskTemplate);

    List<PartMapping> findByVehicleModel(VehicleModel vehicleModel);

    List<PartMapping> findByWorkTaskTemplateAndVehicleModel(WorkTaskTemplate template, VehicleModel vehicleModel);
}
