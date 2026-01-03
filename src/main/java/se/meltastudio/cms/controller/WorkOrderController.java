package se.meltastudio.cms.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.meltastudio.cms.dto.PartOrderRequest;
import se.meltastudio.cms.dto.WorkOrderDTO;
import se.meltastudio.cms.dto.WorkOrderStatusDTO;
import se.meltastudio.cms.model.*;
import se.meltastudio.cms.dto.PartOrderDTO;
import se.meltastudio.cms.security.CustomUserDetails;
import se.meltastudio.cms.service.PartOrderService;
import se.meltastudio.cms.service.WorkOrderService;
import se.meltastudio.cms.service.WorkOrderStatusService;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workorders")
@CrossOrigin(origins = "*")
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private  final WorkOrderStatusService workOrderStatusService;

    private final PartOrderService partOrderService;

    public WorkOrderController(WorkOrderService workOrderService, WorkOrderStatusService workOrderStatusService, PartOrderService partOrderService) {
        this.workOrderService = workOrderService;
        this.workOrderStatusService = workOrderStatusService;
        this.partOrderService = partOrderService;
    }

    @GetMapping
    public ResponseEntity<List<WorkOrderDTO>> getAllWorkOrders() {
        List<WorkOrder> workOrders = workOrderService.getAllWorkOrders();
        List<WorkOrderDTO> dtos = new ArrayList<>();
        for (WorkOrder wo : workOrders) {
            dtos.add(workOrderService.toDTO(wo));
        }
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderDTO> getWorkOrderById(@PathVariable Long id) {
        return workOrderService.getWorkOrderById(id)
                .map(workOrderService::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Hämta en enskild arbetsorder via ID
    @GetMapping("/details/{id}")
    public ResponseEntity<WorkOrderDTO> getWorkOrder(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.getDetailedWorkOrderById(id));
    }


    // Uppdatera arbetsorder (tar emot DTO)
    @PutMapping("/{id}")
    public ResponseEntity<WorkOrderDTO> updateWorkOrder(@PathVariable Long id, @RequestBody WorkOrderDTO workOrderDTO) {
        // Om du har en metod i din service som tar emot en DTO och uppdaterar entiteten:
        WorkOrder updatedWorkOrder = workOrderService.updateWorkOrder(id, workOrderDTO);
        return ResponseEntity.ok(workOrderService.toDTO(updatedWorkOrder));
    }

    @DeleteMapping("/{id}")
    public void deleteWorkOrder(@PathVariable Long id) {
        workOrderService.deleteWorkOrder(id);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkOrderDTO> updateWorkOrderStatus(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        System.out.println("Received request body: " + request); // Debug-logg

        Object statusIdObj = request.get("statusId");
        if (statusIdObj == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Long statusId = Long.valueOf(statusIdObj.toString());
            WorkOrder updatedWorkOrder = workOrderService.updateWorkOrderStatus(id, statusId);
            return ResponseEntity.ok(workOrderService.toDTO(updatedWorkOrder));
        } catch (NumberFormatException e) {
            System.err.println("Invalid statusId format: " + statusIdObj);
            return ResponseEntity.badRequest().build();
        }
    }



    @GetMapping("/workplace/{workplaceId}")
    public ResponseEntity<List<WorkOrderDTO>> getWorkOrdersByWorkplace(
            @PathVariable Long workplaceId,
            @RequestParam(required = false) WorkOrderStatus status,
            @RequestParam(required = false, defaultValue = "false") boolean sortByLatest,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long mechanicId) {
        List<WorkOrder> orders = workOrderService.getWorkOrdersByWorkplace(
                workplaceId, status, sortByLatest, page, pageSize, startDate, endDate, mechanicId);
        List<WorkOrderDTO> dtos = new ArrayList<>();
        for (WorkOrder wo : orders) {
            dtos.add(workOrderService.toDTO(wo));
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/mechanic/{mechanicId}")
    public ResponseEntity<List<WorkOrderDTO>> getWorkOrdersByMechanic(
            @PathVariable Long mechanicId,
            @RequestParam(required = false) WorkOrderStatus status,
            @RequestParam(required = false, defaultValue = "false") boolean sortByLatest,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<WorkOrder> orders = workOrderService.getWorkOrdersByMechanic(
                mechanicId, status, sortByLatest, page, pageSize, startDate, endDate);
        List<WorkOrderDTO> dtos = new ArrayList<>();
        for (WorkOrder wo : orders) {
            dtos.add(workOrderService.toDTO(wo));
        }
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/vehicle/{vehicleId}")
    public ResponseEntity<WorkOrderDTO> addWorkOrder(
            @PathVariable Long vehicleId,
            @RequestBody WorkOrderDTO workOrderDTO,
            @AuthenticationPrincipal CustomUserDetails user) {

        if (!user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endast administratörer kan skapa arbetsordrar.");
        }

        System.out.println("Förbi admin, vad är problemet?" + vehicleId);

        WorkOrder newWorkOrder = workOrderService.addWorkOrder(vehicleId, workOrderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(workOrderService.toDTO(newWorkOrder));
    }


    // Hämta alla arbetsordrar kopplade till ett fordon
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<WorkOrderDTO>> getWorkOrders(@PathVariable Long vehicleId) {
        List<WorkOrder> orders = workOrderService.getWorkOrdersByVehicle(vehicleId);
        List<WorkOrderDTO> dtos = new ArrayList<>();
        for (WorkOrder wo : orders) {
            dtos.add(workOrderService.toDTO(wo));
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<WorkOrderCategory>> getCategories() {
        return ResponseEntity.ok(workOrderService.getAllCategories());
    }

    @GetMapping("/generate-tasks/{workOrderId}")
    public ResponseEntity<List<WorkTask>> generateTasksForWorkOrder(@PathVariable Long workOrderId) {
        List<WorkTask> tasks = workOrderService.generateTasksForWorkOrder(workOrderId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/statuses")
    public List<WorkOrderStatusDTO> getAllStatuses() {
        return workOrderService.getAllWorkOrderStatuses();
    }

    @PatchMapping("/{id}/assign-mechanic")
    public ResponseEntity<WorkOrderDTO> assignMechanic(@PathVariable Long id, @RequestBody Long mechanicId) {
        WorkOrderDTO updatedWorkOrder = workOrderService.assignMechanic(id, mechanicId);

        return ResponseEntity.ok(updatedWorkOrder);
    }

    @GetMapping("/{id}/available-mechanics")
    public ResponseEntity<List<User>> getAvailableMechanics(@PathVariable Long id) {
        List<User> mechanics = workOrderService.getMechanicsForWorkOrder(id);
        return ResponseEntity.ok(mechanics);
    }

    @PostMapping("/{id}/order-part")
    public ResponseEntity<PartOrderDTO> orderPart(@PathVariable Long id, @RequestBody PartOrderRequest request) {
        PartOrderDTO partOrder = workOrderService.orderPart(id, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(partOrder);
    }




}
