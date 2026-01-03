package se.meltastudio.cms.service;

import org.springframework.stereotype.Service;
import se.meltastudio.cms.dto.CompanyDTO;
import se.meltastudio.cms.model.*;
import se.meltastudio.cms.repository.CompanyRepository;
import se.meltastudio.cms.repository.EndCustomerRepository;
import se.meltastudio.cms.repository.VehicleRepository;
import se.meltastudio.cms.repository.WorkplaceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    private final EndCustomerRepository endCustomerRepository;
    private final VehicleRepository vehicleRepository;

    private final WorkplaceRepository workplaceRepository;


    public CompanyService(CompanyRepository companyRepository, EndCustomerRepository endCustomerRepository, VehicleRepository vehicleRepository, WorkplaceRepository workplaceRepository) {
        this.companyRepository = companyRepository;
        this.endCustomerRepository = endCustomerRepository;
        this.vehicleRepository = vehicleRepository;
        this.workplaceRepository = workplaceRepository;
    }

    public List<CompanyDTO> getAllCompanies() {

        List<Company> companies = companyRepository.findAll();
        List<CompanyDTO> dtos = new ArrayList<>();
        for(int i = 0; i<companies.size();i++) {
            dtos.add(toDTO(companies.get(i)));
        }
        return dtos;
    }

    public CompanyDTO getCompanyById(Long id) {
        return toDTO(companyRepository.findById(id).get());
    }

    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    public Optional<Company> updateCompany(Long id, Company companyDetails) {
        return companyRepository.findById(id).map(existingCustomer -> {
            existingCustomer.setName(companyDetails.getName());
            existingCustomer.setOrgNumber(companyDetails.getOrgNumber());
            existingCustomer.setPhone(companyDetails.getPhone());
            existingCustomer.setEmail(companyDetails.getEmail());
            existingCustomer.setAddress(companyDetails.getAddress());

            // Billing information
            existingCustomer.setBankgiro(companyDetails.getBankgiro());
            existingCustomer.setPlusgiro(companyDetails.getPlusgiro());
            existingCustomer.setVatNumber(companyDetails.getVatNumber());
            existingCustomer.setPaymentTerms(companyDetails.getPaymentTerms());
            existingCustomer.setGln(companyDetails.getGln());
            existingCustomer.setBillingStreet(companyDetails.getBillingStreet());
            existingCustomer.setBillingCity(companyDetails.getBillingCity());
            existingCustomer.setBillingZip(companyDetails.getBillingZip());
            existingCustomer.setBillingCountry(companyDetails.getBillingCountry());

            return companyRepository.save(existingCustomer);
        });
    }

    public boolean deleteCompany(Long id) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public CompanyDTO toDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setOrgNumber(company.getOrgNumber());
        dto.setPhone(company.getPhone());
        dto.setEmail(company.getEmail());
        dto.setAddress(company.getAddress());

        // Billing information
        dto.setBankgiro(company.getBankgiro());
        dto.setPlusgiro(company.getPlusgiro());
        dto.setVatNumber(company.getVatNumber());
        dto.setPaymentTerms(company.getPaymentTerms());
        dto.setGln(company.getGln());
        dto.setBillingStreet(company.getBillingStreet());
        dto.setBillingCity(company.getBillingCity());
        dto.setBillingZip(company.getBillingZip());
        dto.setBillingCountry(company.getBillingCountry());

        // Settings
        dto.setCustomerInactiveDays(company.getCustomerInactiveDays());
        dto.setCustomerGdprDeletionDays(company.getCustomerGdprDeletionDays());

        if (company.getWorkplaces() != null) {

            dto.setWorkplaceIds(
                    company.getWorkplaces().stream()
                            .map(Workplace::getId)
                            .collect(Collectors.toList())
            );
        }
        if (company.getEndCustomers() != null) {
            dto.setEndCustomerIds(
                    company.getEndCustomers().stream()
                            .map(EndCustomer::getId)
                            .collect(Collectors.toList())
            );
        }
        if (company.getUsers() != null) {
            dto.setUserIds(
                    company.getUsers().stream()
                            .map(User::getId)
                            .collect(Collectors.toList())
            );
        }
        if (company.getVehicles() != null) {
            dto.setVehicleIds(
                    company.getVehicles().stream()
                            .map(Vehicle::getId)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public List<CompanyDTO> getAllCompanyDTOs() {
        return companyRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Company toCompany(CompanyDTO dto) {
        // Hämta existerande eller skapa en ny entitet
        Company entity = new Company();
        // Om du ska uppdatera existerande, hämta den från DB via repository
        entity.setAddress(dto.getAddress());
        entity.setEmail(dto.getEmail());
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setOrgNumber(dto.getOrgNumber());
        List<Long>endCustomerIds = dto.getEndCustomerIds();
        List<EndCustomer> endCustomers = new ArrayList<>();
        for(int i = 0; i <endCustomerIds.size(); i++) {
            Optional<EndCustomer> tmp = endCustomerRepository.findById(endCustomerIds.get(i));

            endCustomers.add(tmp.get());
        }
        entity.setEndCustomers(endCustomers);

        List<Long>vehicleIds = dto.getVehicleIds();
        List<Vehicle> vehicles = new ArrayList<>();
        for(int i = 0; i <vehicleIds.size(); i++) {
            Optional<Vehicle> tmp = vehicleRepository.findById(vehicleIds.get(i));

            vehicles.add(tmp.get());
        }
        entity.setVehicles(vehicles);


        List<Long>workplaceIds = dto.getWorkplaceIds();
        List<Workplace> workplaces = new ArrayList<>();
        for(int i = 0; i <workplaceIds.size(); i++) {
            Optional<Workplace> tmp = workplaceRepository.findById(workplaceIds.get(i));

            workplaces.add(tmp.get());
        }

        entity.setWorkplaces(workplaces);

        return entity;
    }

    /**
     * Update company settings (e.g., customerInactiveDays, customerGdprDeletionDays).
     * Only SUPER_ADMIN should be able to call this.
     */
    public CompanyDTO updateCompanySettings(Long companyId, Integer customerInactiveDays, Integer customerGdprDeletionDays) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);

        if (optionalCompany.isEmpty()) {
            return null;
        }

        Company company = optionalCompany.get();
        company.setCustomerInactiveDays(customerInactiveDays);
        company.setCustomerGdprDeletionDays(customerGdprDeletionDays);

        Company updated = companyRepository.save(company);
        return toDTO(updated);
    }
}
