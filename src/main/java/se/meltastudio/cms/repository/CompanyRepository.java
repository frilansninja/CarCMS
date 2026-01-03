package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.meltastudio.cms.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
