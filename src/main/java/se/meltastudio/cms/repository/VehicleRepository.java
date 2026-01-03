package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.meltastudio.cms.model.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByEndCustomerId(Long endCustomerId);
    List<Vehicle> findByWorkplaceId(Long workplaceId);

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
    List<Vehicle> findByCompanyId(Long companyId);


}
