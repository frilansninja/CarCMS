package se.meltastudio.cms.parts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.meltastudio.cms.model.Company;
import se.meltastudio.cms.parts.domain.CustomerSupplierConfig;

import java.util.List;
import java.util.Optional;

/**
 * Repository for customer supplier configuration.
 */
@Repository
public interface CustomerSupplierConfigRepository extends JpaRepository<CustomerSupplierConfig, Long> {

    /**
     * Find all enabled suppliers for a company, ordered by priority.
     */
    @Query("SELECT c FROM CustomerSupplierConfig c WHERE c.company = :company AND c.enabled = true ORDER BY c.priority ASC")
    List<CustomerSupplierConfig> findEnabledSuppliersByCompanyOrderByPriority(@Param("company") Company company);

    /**
     * Find specific supplier config for a company.
     */
    Optional<CustomerSupplierConfig> findByCompanyAndSupplierCode(Company company, String supplierCode);

    /**
     * Find all supplier configs for a company.
     */
    List<CustomerSupplierConfig> findByCompanyOrderByPriorityAsc(Company company);
}
