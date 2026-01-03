package se.meltastudio.cms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.meltastudio.cms.dto.CompanyDTO;
import se.meltastudio.cms.dto.WorkplaceDTO;
import se.meltastudio.cms.model.Company;
import se.meltastudio.cms.model.Vehicle;
import se.meltastudio.cms.model.Workplace;
import se.meltastudio.cms.service.CompanyService;
import se.meltastudio.cms.service.VehicleService;
import se.meltastudio.cms.service.WorkplaceService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workplaces")
@CrossOrigin(origins = "*")
public class WorkplaceController {

    private final WorkplaceService workplaceService;
    private final CompanyService companyService;
    private final VehicleService vehicleService;

    public WorkplaceController(WorkplaceService workplaceService, CompanyService companyService, VehicleService vehicleService) {
        this.workplaceService = workplaceService;
        this.companyService = companyService;
        this.vehicleService = vehicleService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<WorkplaceDTO>> getWorkplaces(@PathVariable Long customerId) {
        List<Workplace> workplaces = workplaceService.getWorkplacesByCustomer(customerId);
        List<WorkplaceDTO> dtos = workplaces.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkplace(@PathVariable Long id) {
        workplaceService.deleteWorkplace(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkplaceDTO> updateWorkplace(@PathVariable Long id, @RequestBody WorkplaceDTO dto) {
        // Kontrollera att workplace finns
        Optional<Workplace> existingOpt = workplaceService.getWorkplaceById(id);
        if (!existingOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Skapa ett workplace-objekt med endast de fält som ska uppdateras
        Workplace workplaceToUpdate = new Workplace();
        workplaceToUpdate.setName(dto.getName());
        workplaceToUpdate.setAddress(dto.getAddress());
        workplaceToUpdate.setCity(dto.getCity());
        workplaceToUpdate.setZipCode(dto.getZipCode());
        workplaceToUpdate.setCountry(dto.getCountry());
        workplaceToUpdate.setPhone(dto.getPhone());
        workplaceToUpdate.setEmail(dto.getEmail());

        Workplace updatedWorkplace = workplaceService.updateWorkplace(id, workplaceToUpdate);
        WorkplaceDTO updatedDto = convertEntityToDto(updatedWorkplace);
        return ResponseEntity.ok(updatedDto);
    }


    @GetMapping
    public ResponseEntity<List<WorkplaceDTO>> getAllWorkplaces() {
        List<Workplace> workplaces = workplaceService.getAllWorkplaces();
        List<WorkplaceDTO> dtos = workplaces.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{companyId}")
    public ResponseEntity<WorkplaceDTO> createWorkplace(@PathVariable Long companyId, @RequestBody WorkplaceDTO dto) {
        if(dto.getCompanyId() == null) {
            dto.setCompanyId(companyId);
        }
        Workplace workplaceToCreate = convertDtoToEntity(dto);
        Workplace savedWorkplace = workplaceService.addWorkplace(companyId, workplaceToCreate);
        WorkplaceDTO savedDto = convertEntityToDto(savedWorkplace);
        return ResponseEntity.ok(savedDto);
    }

    // Hjälpmetoder för konvertering mellan entitet och DTO

    private WorkplaceDTO convertEntityToDto(Workplace workplace) {
        WorkplaceDTO dto = new WorkplaceDTO();
        dto.setId(workplace.getId());
        dto.setName(workplace.getName());

        // Adressinformation
        dto.setAddress(workplace.getAddress());
        dto.setCity(workplace.getCity());
        dto.setZipCode(workplace.getZipCode());
        dto.setCountry(workplace.getCountry());
        dto.setPhone(workplace.getPhone());
        dto.setEmail(workplace.getEmail());

        // Om din Workplace har en koppling till Company:
        if (workplace.getCompany() != null) {
            dto.setCompanyId(workplace.getCompany().getId());
        }
        // Om din Workplace har en lista med fordon:
        if (workplace.getVehicles() != null) {
            dto.setVehicleIds(
                    workplace.getVehicles().stream()
                            .map(vehicle -> vehicle.getId())
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    private Workplace convertDtoToEntity(WorkplaceDTO dto) {
        Workplace workplace = new Workplace();
        workplace.setName(dto.getName());

        // Adressinformation
        workplace.setAddress(dto.getAddress());
        workplace.setCity(dto.getCity());
        workplace.setZipCode(dto.getZipCode());
        workplace.setCountry(dto.getCountry());
        workplace.setPhone(dto.getPhone());
        workplace.setEmail(dto.getEmail());

        CompanyDTO company = companyService.getCompanyById(dto.getCompanyId());
        if(company == null)
            return null;

        workplace.setCompany(companyService.toCompany(company));
        List<Vehicle> vs = vehicleService.getVehiclesByWorkPlace(dto.getId());
        workplace.setVehicles(vs);
        return workplace;
    }
}
