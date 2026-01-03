package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.meltastudio.cms.model.Workplace;

import java.util.List;

public interface WorkplaceRepository extends JpaRepository<Workplace, Long> {
    List<Workplace> findByCompanyId(Long companyId);
}
