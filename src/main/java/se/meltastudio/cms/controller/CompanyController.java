package se.meltastudio.cms.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.meltastudio.cms.dto.CompanyDTO;
import se.meltastudio.cms.model.Company;
import se.meltastudio.cms.model.User;
import se.meltastudio.cms.service.CompanyService;
import se.meltastudio.cms.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final UserService userService;

    public CompanyController(CompanyService companyService, UserService userService) {
        this.companyService = companyService;
        this.userService = userService;
    }

    // 1️⃣ GET - Hämta alla kunder
    // 🔹 Tillåt både ADMIN och OFFICE att hämta alla kunder
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CUSTOMER_ADMIN', 'WORKPLACE_ADMIN', 'OFFICE')")
    @GetMapping
    public List<CompanyDTO> getCompanies() {
        return companyService.getAllCompanies();
    }

    // 2️⃣ GET - Hämta en kund via ID
    // 🔹 Tillåt både ADMIN och OFFICE att hämta alla kunder
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CUSTOMER_ADMIN', 'WORKPLACE_ADMIN', 'OFFICE')")
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long id) {
        CompanyDTO companyOpt = companyService.getCompanyById(id);

        if (companyOpt == null)
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(companyOpt);
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CUSTOMER_ADMIN', 'WORKPLACE_ADMIN')")
    @PostMapping
    public ResponseEntity<CompanyDTO> addCompany(@RequestBody Company company) {
        Company savedCompany = companyService.saveCompany(company);
        return ResponseEntity.ok(companyService.toDTO(savedCompany));
    }

    // 4️⃣ PUT - Uppdatera en kund
    // 🔹 Endast ADMIN kan uppdatera en kund
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CUSTOMER_ADMIN', 'WORKPLACE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Long id, @RequestBody CompanyDTO companyDTO) {
        // Konvertera DTO till entitet (utan relationer)
        Company companyDetails = new Company();
        companyDetails.setName(companyDTO.getName());
        companyDetails.setOrgNumber(companyDTO.getOrgNumber());
        companyDetails.setPhone(companyDTO.getPhone());
        companyDetails.setEmail(companyDTO.getEmail());
        companyDetails.setAddress(companyDTO.getAddress());
        companyDetails.setBankgiro(companyDTO.getBankgiro());
        companyDetails.setPlusgiro(companyDTO.getPlusgiro());
        companyDetails.setVatNumber(companyDTO.getVatNumber());
        companyDetails.setPaymentTerms(companyDTO.getPaymentTerms());
        companyDetails.setGln(companyDTO.getGln());
        companyDetails.setBillingStreet(companyDTO.getBillingStreet());
        companyDetails.setBillingCity(companyDTO.getBillingCity());
        companyDetails.setBillingZip(companyDTO.getBillingZip());
        companyDetails.setBillingCountry(companyDTO.getBillingCountry());

        Optional<Company> updatedCompany = companyService.updateCompany(id, companyDetails);

        if(updatedCompany.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        CompanyDTO dto = companyService.toDTO(updatedCompany.get());
        return ResponseEntity.ok(dto);

    }

    // 5️⃣ DELETE - Ta bort en kund
    // 🔹 Endast ADMIN kan ta bort en kund
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CUSTOMER_ADMIN', 'WORKPLACE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        boolean deleted = companyService.deleteCompany(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // 🔹 Endast ADMIN och OFFICE kan hämta användare för en kund
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CUSTOMER_ADMIN', 'WORKPLACE_ADMIN', 'OFFICE')")
    @GetMapping("/{id}/users")
    public ResponseEntity<Page<se.meltastudio.cms.dto.UserDTO>> getUsersByCompany(
            @PathVariable Long id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long workplaceId,
            Pageable pageable) {

        System.out.println("companycontroller getUsersByCompany" + id);

        CompanyDTO company = companyService.getCompanyById(id);

        if (company == null){
            return ResponseEntity.notFound().build();
        }


        Page<User> users = userService.searchUsers(company, search, role, workplaceId, pageable);
        Page<se.meltastudio.cms.dto.UserDTO> userDTOs = users.map(userService::toDTO);
        return ResponseEntity.ok(userDTOs);
    }

    // 🔹 Endast SUPER_ADMIN kan uppdatera företagsinställningar
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{id}/settings")
    public ResponseEntity<CompanyDTO> updateCompanySettings(
            @PathVariable Long id,
            @RequestBody CompanySettingsRequest settings) {

        CompanyDTO updated = companyService.updateCompanySettings(
                id,
                settings.getCustomerInactiveDays(),
                settings.getCustomerGdprDeletionDays()
        );

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);
    }
}

// DTO för company settings request
class CompanySettingsRequest {
    private Integer customerInactiveDays;
    private Integer customerGdprDeletionDays;

    public Integer getCustomerInactiveDays() {
        return customerInactiveDays;
    }

    public void setCustomerInactiveDays(Integer customerInactiveDays) {
        this.customerInactiveDays = customerInactiveDays;
    }

    public Integer getCustomerGdprDeletionDays() {
        return customerGdprDeletionDays;
    }

    public void setCustomerGdprDeletionDays(Integer customerGdprDeletionDays) {
        this.customerGdprDeletionDays = customerGdprDeletionDays;
    }
}


