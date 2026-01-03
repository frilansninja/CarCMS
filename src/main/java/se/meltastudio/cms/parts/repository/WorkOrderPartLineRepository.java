package se.meltastudio.cms.parts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.meltastudio.cms.model.WorkOrder;
import se.meltastudio.cms.parts.domain.PartLineStatus;
import se.meltastudio.cms.parts.domain.WorkOrderPartLine;

import java.util.List;

/**
 * Repository for work order part lines.
 */
@Repository
public interface WorkOrderPartLineRepository extends JpaRepository<WorkOrderPartLine, Long> {

    /**
     * Find all part lines for a specific work order.
     */
    List<WorkOrderPartLine> findByWorkOrderOrderByCreatedAtDesc(WorkOrder workOrder);

    /**
     * Find part lines by work order and status.
     */
    List<WorkOrderPartLine> findByWorkOrderAndStatus(WorkOrder workOrder, PartLineStatus status);

    /**
     * Count part lines for a work order.
     */
    long countByWorkOrder(WorkOrder workOrder);
}
