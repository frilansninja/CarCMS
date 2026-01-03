package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.meltastudio.cms.model.RepairCategory;
import se.meltastudio.cms.model.Role;

import java.util.Optional;

@Repository
public interface RepairCategoryRepository extends JpaRepository<RepairCategory, Long> {
}
