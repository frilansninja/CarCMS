package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.meltastudio.cms.model.WorkOrderCategory;

public interface WorkOrderCategoryRepository extends JpaRepository<WorkOrderCategory, Long> {
}
