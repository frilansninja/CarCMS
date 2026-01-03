package se.meltastudio.cms.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.meltastudio.cms.dto.VehicleDTO;
import se.meltastudio.cms.model.WorkOrderStatus;
import se.meltastudio.cms.model.Vehicle;
import se.meltastudio.cms.security.CustomUserDetails;
import se.meltastudio.cms.service.VehicleService;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/endcustomer/{endCustomerId}")
    public List<Vehicle> getVehicles(@PathVariable Long endCustomerId) {
        return vehicleService.getVehiclesByEndCustomer(endCustomerId);
    }

    @PostMapping("/{endCustomerId}")
    public Vehicle addVehicle(@PathVariable Long endCustomerId, @RequestBody Vehicle vehicle) {
        return vehicleService.addVehicle(endCustomerId, vehicle);
    }



    @PutMapping("/{id}")
    public Vehicle updateVehicle(@PathVariable Long id,
                                 @RequestBody Vehicle updatedVehicle,
                                 @AuthenticationPrincipal CustomUserDetails user) {
        if (!user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endast administratörer kan uppdatera fordon.");
        }

        return vehicleService.updateVehicle(id, updatedVehicle);
    }




    @GetMapping("/details/{id}")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }





    @DeleteMapping("/{id}")
    public void deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }

    @GetMapping("/workplace/{workplaceId}")
    public List<Map<String, Object>> getVehiclesWithStatusByWorkplace(
            @PathVariable Long workplaceId,
            @RequestParam(required = false) WorkOrderStatus status,
            @RequestParam(required = false, defaultValue = "false") boolean sortByLatest,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Om inga filter används, hämta alla fordon
        if (status == null && !sortByLatest && page == null && pageSize == null && startDate == null && endDate == null) {
            return vehicleService.getVehiclesWithStatusByWorkplace(workplaceId);
        }

        // Annars, hämta filtrerade resultat
        return vehicleService.getVehiclesWithStatusByWorkplace(workplaceId, status, sortByLatest, page, pageSize, startDate, endDate);
    }

    @GetMapping("/company/{companyId}")
    public List<VehicleDTO> getVehiclesByCustomer(@PathVariable Long companyId) {
        return vehicleService.getVehiclesByCompany(companyId).stream()
                .map(vehicleService::toDTO)
                .toList();
    }

}
