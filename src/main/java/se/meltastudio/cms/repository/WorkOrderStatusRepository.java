package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se.meltastudio.cms.model.WorkOrderStatus;

import java.util.List;

@Repository
public interface WorkOrderStatusRepository extends JpaRepository<WorkOrderStatus, Long> {

    @Query("SELECT DISTINCT w FROM WorkOrderStatus w WHERE w.id = 1")
    WorkOrderStatus getDefaultStatus();
}

