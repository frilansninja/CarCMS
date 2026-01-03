package se.meltastudio.cms.service;

import org.springframework.stereotype.Service;
import se.meltastudio.cms.model.Company;
import se.meltastudio.cms.model.Workplace;
import se.meltastudio.cms.repository.CompanyRepository;
import se.meltastudio.cms.repository.WorkOrderStatusRepository;
import se.meltastudio.cms.repository.WorkplaceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WorkplaceService {


    private final WorkplaceRepository workplaceRepository;
    private final CompanyRepository companyRepository;

    private final WorkOrderStatusRepository workOrderStatusRepository;


    public WorkplaceService(WorkplaceRepository workplaceRepository, CompanyRepository companyRepository, WorkOrderStatusRepository workOrderStatusRepository) {
        this.workplaceRepository = workplaceRepository;
        this.companyRepository = companyRepository;
        this.workOrderStatusRepository = workOrderStatusRepository;
    }

    public List<Workplace> getWorkplacesByCustomer(Long companyId) {
        return workplaceRepository.findByCompanyId(companyId);
    }

    public Optional<Workplace> getWorkplaceById(Long id) {
        return workplaceRepository.findById(id);
    }


    public Workplace addWorkplace(Long companyId, Workplace workplace) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        workplace.setCompany(company);
        return workplaceRepository.save(workplace);
    }

    public void deleteWorkplace(Long id) {
        workplaceRepository.deleteById(id);
    }

    public Workplace updateWorkplace(Long id, Workplace updatedWorkplace) {
        return workplaceRepository.findById(id)
                .map(workplace -> {
                    workplace.setName(updatedWorkplace.getName());
                    workplace.setAddress(updatedWorkplace.getAddress());
                    workplace.setCity(updatedWorkplace.getCity());
                    workplace.setZipCode(updatedWorkplace.getZipCode());
                    workplace.setCountry(updatedWorkplace.getCountry());
                    workplace.setPhone(updatedWorkplace.getPhone());
                    workplace.setEmail(updatedWorkplace.getEmail());
                    // Uppdatera inte Company och Vehicles - de hanteras separat
                    return workplaceRepository.save(workplace);
                }).orElseThrow(() -> new RuntimeException("Workplace not found"));
    }

    public List<Workplace> getAllWorkplaces() {
        return workplaceRepository.findAll();
    }


}
