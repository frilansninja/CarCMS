package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.meltastudio.cms.model.RepairCategory;
import se.meltastudio.cms.model.WorkOrderCategory;
import se.meltastudio.cms.model.WorkTaskTemplate;

import java.util.List;

public interface WorkTaskTemplateRepository extends JpaRepository<WorkTaskTemplate, Long> {
    List<WorkTaskTemplate> findByCategory(WorkOrderCategory workOrderCategory);
    List<WorkTaskTemplate> findByRepairCategory(RepairCategory repairCategory);

    @Query("SELECT w FROM WorkTaskTemplate w JOIN FETCH w.category")
    List<WorkTaskTemplate> findAllWithCategory(); // 🔥 Ser till att kategorin laddas

    @Query("SELECT w FROM WorkTaskTemplate w JOIN FETCH w.category WHERE w.category.id = :categoryId")
    List<WorkTaskTemplate> findByCategoryId(@Param("categoryId") Long categoryId);
}
