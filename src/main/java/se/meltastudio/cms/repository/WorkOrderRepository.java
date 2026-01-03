package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.meltastudio.cms.model.WorkOrder;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByVehicleId(Long vehicleId);

    List<WorkOrder> findByVehicle_EndCustomerId(Long endCustomerId);
    WorkOrder findTopByVehicleIdOrderByCreatedDateDesc(Long vehicleId);
        @Query("SELECT wo FROM WorkOrder wo WHERE wo.vehicle.workplace.id = :workplaceId")
        List<WorkOrder> findByWorkplaceId(@Param("workplaceId") Long workplaceId);

    @Query("SELECT wo FROM WorkOrder wo WHERE wo.vehicle.workplace.id = :workplaceId AND wo.mechanic.id = :mechanicId")
    List<WorkOrder> findByWorkplaceIdAndMechanic(@Param("workplaceId") Long workplaceId, @Param("mechanicId") Long mechanicId);

    @Query("SELECT wo FROM WorkOrder wo WHERE wo.mechanic.id = :mechanicId")
    List<WorkOrder> findByMechanicId(@Param("mechanicId") Long mechanicId);


    @Query("SELECT w FROM WorkOrder w " +
            "LEFT JOIN FETCH w.vehicle " +
            "LEFT JOIN FETCH w.mechanic " +
            "LEFT JOIN FETCH w.status " +
            "LEFT JOIN FETCH w.category " +
            "LEFT JOIN FETCH w.partOrders " +
            "WHERE w.id = :id")
    Optional<WorkOrder> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT w FROM WorkOrder w WHERE w.vehicle.id = :vehicleId AND w.category.name = 'Service' ORDER BY w.createdDate DESC LIMIT 1")
    Optional<WorkOrder> findLatestServiceByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query("SELECT w FROM WorkOrder w WHERE w.vehicle.endCustomer.id = :id")
    List<WorkOrder> findByEndCustomerId(Long id);
}