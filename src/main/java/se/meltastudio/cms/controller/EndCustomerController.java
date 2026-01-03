package se.meltastudio.cms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.meltastudio.cms.dto.EndCustomerDTO;
import se.meltastudio.cms.dto.InvoiceDTO;
import se.meltastudio.cms.dto.VehicleDTO;
import se.meltastudio.cms.dto.WorkOrderDTO;
import se.meltastudio.cms.model.EndCustomer;
import se.meltastudio.cms.model.Invoice;
import se.meltastudio.cms.model.Vehicle;
import se.meltastudio.cms.model.WorkOrder;
import se.meltastudio.cms.security.CustomUserDetails;
import se.meltastudio.cms.service.EndCustomerService;
import se.meltastudio.cms.service.InvoiceService;
import se.meltastudio.cms.service.VehicleService;
import se.meltastudio.cms.service.WorkOrderService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/endcustomers")
@CrossOrigin(origins = "*")
public class EndCustomerController {

    private final EndCustomerService endCustomerService;
    private final VehicleService vehicleService;
    private final WorkOrderService workOrderService;
    private final InvoiceService invoiceService;

    public EndCustomerController(
            EndCustomerService endCustomerService,
            VehicleService vehicleService,
            WorkOrderService workOrderService,
            InvoiceService invoiceService
    ) {
        this.endCustomerService = endCustomerService;
        this.vehicleService = vehicleService;
        this.workOrderService = workOrderService;
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public List<EndCustomerDTO> getEndCustomers(@AuthenticationPrincipal CustomUserDetails user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ingen användare är inloggad");
        }
        Long companyId = user.getCompanyId();
        List<EndCustomer> endCustomers = endCustomerService.getEndCustomersByCompany(companyId);
        return endCustomers.stream().map(endCustomerService::toDTO).toList();
    }


    /**
     * Hämta en enskild slutkund (DTO) baserat på id.
     * Returnerar enbart EndCustomerDTO (ingen JPA‑entitet).
     */
    @GetMapping("/details/{id}")
    public EndCustomerDTO getEndCustomerById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ingen användare är inloggad");
        }
        Long companyId = user.getCompanyId();
        return endCustomerService.toDTO(endCustomerService.getEndCustomerById(id, companyId));
    }

    /**
     * Hämta alla fordon (VehicleDTO) för en slutkund, returnerar enbart DTO.
     */
    @GetMapping("/{id}/vehicles")
    public List<VehicleDTO> getVehiclesByEndCustomer(@PathVariable Long id) {
        var vehicles = endCustomerService.getVehiclesByEndCustomer(id);
        System.out.println("vehi " + vehicles.size());
        List<VehicleDTO> dtos = new ArrayList<>();
        vehicles.forEach(v -> dtos.add(vehicleService.toDTO(v)));
        System.out.println("dt " + dtos.size());
        return dtos;
    }

    /**
     * Lägg till ett fordon för en kund, tar emot VehicleDTO i requestbody.
     * Returnerar VehicleDTO. (Ingen JPA-entitet exponerad)
     */
    @PostMapping("/{id}/vehicles")
    public VehicleDTO addVehicleToEndCustomer(@PathVariable Long id, @RequestBody VehicleDTO vehicleDto) {
        // Mappa VehicleDTO -> Vehicle:
        Vehicle vehicleEntity = vehicleService.toEntity(vehicleDto);

        // Koppla fordonet till endCustomer i service:
        var saved = endCustomerService.addVehicleToEndCustomer(id, vehicleEntity);

        // Returnera VehicleDTO
        return vehicleService.toDTO(saved);
    }

    /**
     * Hämta arbetsordrar (WorkOrderDTO) för en kund, returnerar enbart DTO.
     */
    @GetMapping("/{id}/workorders")
    public List<WorkOrderDTO> getWorkOrdersByEndCustomer(@PathVariable Long id) {
        List<WorkOrder> workOrders = endCustomerService.getWorkOrdersByEndCustomer(id);
        List<WorkOrderDTO> dtos = new ArrayList<>();
        workOrders.forEach(wo -> dtos.add(workOrderService.toDTO(wo)));
        return dtos;
    }

    /**
     * Hämta fakturor (InvoiceDTO) för en kund, returnerar enbart DTO.
     */
    @GetMapping("/{id}/invoices")
    public List<InvoiceDTO> getInvoicesByEndCustomer(@PathVariable Long id) {
        List<Invoice> invoices = endCustomerService.getInvoicesByEndCustomer(id);
        List<InvoiceDTO> dtos = new ArrayList<>();
        invoices.forEach(inv -> dtos.add(invoiceService.toDTO(inv)));
        return dtos;
    }

    /**
     * Skapa ny slutkund. Tar emot EndCustomerDTO i requestbody, returnerar EndCustomerDTO.
     */
    @PostMapping("/{customerId}")
    public EndCustomerDTO addEndCustomer(
            @PathVariable Long customerId,
            @RequestBody EndCustomerDTO endCustomerDto
    ) {
        // Lägg till slutkund i service, men arbeta med DTOer
        var createdDto = endCustomerService.addEndCustomerDTO(customerId, endCustomerDto);
        return createdDto;
    }

    /**
     * Uppdatera en slutkund. Tar emot EndCustomerDTO i requestbody, returnerar EndCustomerDTO.
     */
    @PutMapping("/{id}")
    public EndCustomerDTO updateEndCustomer(
            @PathVariable Long id,
            @RequestBody EndCustomerDTO updatedCustomerDto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (user == null || !user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endast administratörer kan uppdatera kunder.");
        }

        return endCustomerService.updateEndCustomerDTO(id, updatedCustomerDto);
    }

    /**
     * Radera en slutkund.
     */
    @DeleteMapping("/{id}")
    public void deleteEndCustomer(@PathVariable Long id) {
        endCustomerService.deleteEndCustomer(id);
    }

    /**
     * Archive (soft delete) an end customer.
     * Sets isActive = false and archivedDate = now
     */
    @PatchMapping("/{id}/archive")
    public EndCustomerDTO archiveEndCustomer(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ingen användare är inloggad");
        }
        Long companyId = user.getCompanyId();
        return endCustomerService.archiveEndCustomer(id, companyId);
    }

    /**
     * Unarchive (restore) an end customer.
     * Sets isActive = true and archivedDate = null
     */
    @PatchMapping("/{id}/unarchive")
    public EndCustomerDTO unarchiveEndCustomer(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ingen användare är inloggad");
        }
        Long companyId = user.getCompanyId();
        return endCustomerService.unarchiveEndCustomer(id, companyId);
    }

    /**
     * Get all end customers including archived ones (for admin views).
     * Add query parameter includeArchived=true to include archived customers.
     */
    @GetMapping("/all")
    public List<EndCustomerDTO> getAllEndCustomers(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false, defaultValue = "false") boolean includeArchived
    ) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ingen användare är inloggad");
        }
        Long companyId = user.getCompanyId();
        List<EndCustomer> endCustomers;

        if (includeArchived) {
            endCustomers = endCustomerService.getAllEndCustomersIncludingArchived(companyId);
        } else {
            endCustomers = endCustomerService.getActiveEndCustomers(companyId);
        }

        return endCustomers.stream().map(endCustomerService::toDTO).toList();
    }
}
