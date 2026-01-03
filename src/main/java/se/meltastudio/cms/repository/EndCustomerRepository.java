package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.meltastudio.cms.enums.WorkOrderStatus;
import se.meltastudio.cms.model.EndCustomer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EndCustomerRepository extends JpaRepository<EndCustomer, Long> {
    @Query("SELECT e FROM EndCustomer e WHERE e.company.id = :companyId AND EXISTS ("
            + "SELECT v FROM Vehicle v WHERE v.endCustomer = e AND EXISTS ("
            + "SELECT w FROM WorkOrder w WHERE w.vehicle = v AND w.status IN :statusList))")
    List<EndCustomer> findByCompanyIdAndHasActiveWorkOrders(@Param("companyId") Long companyId, @Param("statusList") List<WorkOrderStatus> statusList);

    @Query("SELECT e FROM EndCustomer e WHERE e.id = :id AND e.company.id = :companyId")
    Optional<EndCustomer> findByIdAndCompanyId(@Param("id") Long id, @Param("companyId") Long companyId);

    List<EndCustomer> findByCompanyId(Long companyId);

    // Find only active (non-archived) customers
    List<EndCustomer> findByCompanyIdAndIsActive(Long companyId, Boolean isActive);

    // Find all customers (including archived) - useful for admin views
    @Query("SELECT e FROM EndCustomer e WHERE e.company.id = :companyId")
    List<EndCustomer> findAllByCompanyIdIncludingArchived(@Param("companyId") Long companyId);

    // Find archived customers that were archived before a specific date (for GDPR anonymization)
    List<EndCustomer> findByCompanyIdAndIsActiveAndArchivedDateBefore(
            Long companyId,
            Boolean isActive,
            LocalDateTime archivedDate
    );

}
