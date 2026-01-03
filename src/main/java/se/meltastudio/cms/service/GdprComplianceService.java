package se.meltastudio.cms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.meltastudio.cms.model.Company;
import se.meltastudio.cms.model.EndCustomer;
import se.meltastudio.cms.repository.CompanyRepository;
import se.meltastudio.cms.repository.EndCustomerRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for GDPR compliance operations, including automatic
 * anonymization of end customers after a configurable period of inactivity.
 */
@Service
public class GdprComplianceService {

    private static final Logger logger = LoggerFactory.getLogger(GdprComplianceService.class);

    private final CompanyRepository companyRepository;
    private final EndCustomerRepository endCustomerRepository;

    public GdprComplianceService(CompanyRepository companyRepository, EndCustomerRepository endCustomerRepository) {
        this.companyRepository = companyRepository;
        this.endCustomerRepository = endCustomerRepository;
    }

    /**
     * Scheduled task that runs daily at 2:00 AM to check for end customers
     * that need to be anonymized for GDPR compliance.
     */
    @Scheduled(cron = "0 0 2 * * ?") // Runs every day at 2:00 AM
    @Transactional
    public void processGdprAnonymization() {
        logger.info("Starting GDPR anonymization job");

        List<Company> companies = companyRepository.findAll();
        int totalAnonymized = 0;

        for (Company company : companies) {
            // Skip if company hasn't configured GDPR deletion days
            if (company.getCustomerGdprDeletionDays() == null || company.getCustomerGdprDeletionDays() <= 0) {
                continue;
            }

            int anonymizedCount = anonymizeInactiveCustomers(company);
            totalAnonymized += anonymizedCount;

            if (anonymizedCount > 0) {
                logger.info("Anonymized {} customers for company: {}", anonymizedCount, company.getName());
            }
        }

        logger.info("GDPR anonymization job completed. Total customers anonymized: {}", totalAnonymized);
    }

    /**
     * Anonymizes end customers for a specific company based on their GDPR deletion settings.
     *
     * @param company The company whose customers should be checked
     * @return The number of customers anonymized
     */
    private int anonymizeInactiveCustomers(Company company) {
        Integer gdprDeletionDays = company.getCustomerGdprDeletionDays();

        if (gdprDeletionDays == null || gdprDeletionDays <= 0) {
            return 0;
        }

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(gdprDeletionDays);

        // Find archived customers that have passed the GDPR deletion threshold
        List<EndCustomer> customersToAnonymize = endCustomerRepository
                .findByCompanyIdAndIsActiveAndArchivedDateBefore(
                        company.getId(),
                        false,
                        cutoffDate
                );

        int count = 0;
        for (EndCustomer customer : customersToAnonymize) {
            anonymizeCustomer(customer);
            count++;
        }

        return count;
    }

    /**
     * Anonymizes all personal data for a single end customer.
     *
     * @param customer The customer to anonymize
     */
    private void anonymizeCustomer(EndCustomer customer) {
        logger.debug("Anonymizing customer ID: {}", customer.getId());

        // Anonymize personal data
        customer.setName("ANONYMIZED_" + customer.getId());
        customer.setEmail("anonymized_" + customer.getId() + "@gdpr.deleted");
        customer.setPhone("ANONYMIZED");
        customer.setBillingStreet("ANONYMIZED");
        customer.setBillingCity("ANONYMIZED");
        customer.setBillingZip("ANONYMIZED");
        customer.setBillingCountry("ANONYMIZED");

        endCustomerRepository.save(customer);
    }

    /**
     * Manual method to anonymize a specific customer (for testing or manual operations).
     *
     * @param customerId The ID of the customer to anonymize
     */
    @Transactional
    public void manuallyAnonymizeCustomer(Long customerId) {
        EndCustomer customer = endCustomerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        anonymizeCustomer(customer);
        logger.info("Manually anonymized customer ID: {}", customerId);
    }
}
