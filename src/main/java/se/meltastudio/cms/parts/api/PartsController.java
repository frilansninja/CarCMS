package se.meltastudio.cms.parts.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import se.meltastudio.cms.model.User;
import se.meltastudio.cms.model.Vehicle;
import se.meltastudio.cms.model.WorkOrder;
import se.meltastudio.cms.parts.domain.WorkOrderPartLine;
import se.meltastudio.cms.parts.service.PartsService;
import se.meltastudio.cms.parts.service.PartsServiceException;
import se.meltastudio.cms.repository.UserRepository;
import se.meltastudio.cms.repository.WorkOrderRepository;

import java.util.List;

/**
 * REST controller for spare parts functionality.
 * All endpoints are scoped to work orders: /api/work-orders/{workOrderId}/parts/*
 */
@RestController
@RequestMapping("/api/work-orders/{workOrderId}/parts")
public class PartsController {

    private static final Logger log = LoggerFactory.getLogger(PartsController.class);

    private final PartsService partsService;
    private final UserRepository userRepository;
    private final WorkOrderRepository workOrderRepository;

    public PartsController(
            PartsService partsService,
            UserRepository userRepository,
            WorkOrderRepository workOrderRepository) {
        this.partsService = partsService;
        this.userRepository = userRepository;
        this.workOrderRepository = workOrderRepository;
    }

    /**
     * Search for parts for the work order's vehicle.
     * GET /api/work-orders/{workOrderId}/parts/search?q=brake+pad&category=brakes&limit=20
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchParts(
            @PathVariable Long workOrderId,
            @RequestParam String q,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        try {
            User user = getUserFromAuth(userDetails);
            WorkOrder workOrder = getWorkOrderWithAuth(workOrderId, user);

            // Build vehicle context from work order's vehicle
            Vehicle vehicle = workOrder.getVehicle();
            VehicleContext vehicleContext = buildVehicleContext(vehicle);

            // Build search query
            PartSearchQuery query = new PartSearchQuery(q, category, limit);

            // Search parts
            List<PartOffer> results = partsService.searchParts(user.getCompany(), vehicleContext, query);

            log.info("Parts search for work order {}: {} results", workOrderId, results.size());
            return ResponseEntity.ok(results);

        } catch (PartsServiceException e) {
            log.error("Parts search failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during parts search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while searching for parts"));
        }
    }

    /**
     * Add a part line to the work order.
     * POST /api/work-orders/{workOrderId}/parts/lines
     */
    @PostMapping("/lines")
    public ResponseEntity<?> addPartLine(
            @PathVariable Long workOrderId,
            @RequestBody AddPartLineRequest request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        try {
            User user = getUserFromAuth(userDetails);

            WorkOrderPartLine partLine = partsService.addPartLine(workOrderId, request, user);

            log.info("Added part line {} to work order {} by user {}",
                    partLine.getId(), workOrderId, user.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toPartLineResponse(partLine));

        } catch (PartsServiceException e) {
            log.error("Failed to add part line", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error adding part line", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while adding the part"));
        }
    }

    /**
     * Get all part lines for a work order.
     * GET /api/work-orders/{workOrderId}/parts/lines
     */
    @GetMapping("/lines")
    public ResponseEntity<?> getPartLines(
            @PathVariable Long workOrderId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        try {
            User user = getUserFromAuth(userDetails);

            List<WorkOrderPartLine> partLines = partsService.getWorkOrderPartLines(workOrderId, user);

            List<PartLineResponse> response = partLines.stream()
                    .map(this::toPartLineResponse)
                    .toList();

            return ResponseEntity.ok(response);

        } catch (PartsServiceException e) {
            log.error("Failed to get part lines", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error getting part lines", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while retrieving parts"));
        }
    }

    /**
     * Place an order for a part line with the supplier.
     * POST /api/work-orders/{workOrderId}/parts/lines/{lineId}/order
     */
    @PostMapping("/lines/{lineId}/order")
    public ResponseEntity<?> orderPartLine(
            @PathVariable Long workOrderId,
            @PathVariable Long lineId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        try {
            User user = getUserFromAuth(userDetails);

            WorkOrderPartLine partLine = partsService.placeOrder(workOrderId, lineId, user);

            log.info("Placed order for part line {} on work order {} by user {}",
                    lineId, workOrderId, user.getId());

            return ResponseEntity.ok(toPartLineResponse(partLine));

        } catch (PartsServiceException e) {
            log.error("Failed to place order", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error placing order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while placing the order"));
        }
    }

    /**
     * Cancel a part line.
     * DELETE /api/work-orders/{workOrderId}/parts/lines/{lineId}
     */
    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<?> cancelPartLine(
            @PathVariable Long workOrderId,
            @PathVariable Long lineId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        try {
            User user = getUserFromAuth(userDetails);

            partsService.cancelPartLine(workOrderId, lineId, user);

            log.info("Cancelled part line {} on work order {} by user {}",
                    lineId, workOrderId, user.getId());

            return ResponseEntity.noContent().build();

        } catch (PartsServiceException e) {
            log.error("Failed to cancel part line", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error cancelling part line", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while cancelling the part"));
        }
    }

    // Helper methods

    private User getUserFromAuth(org.springframework.security.core.userdetails.UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
    }

    private WorkOrder getWorkOrderWithAuth(Long workOrderId, User user) throws PartsServiceException {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new PartsServiceException("Work order not found: " + workOrderId));

        // Verify access
        if (!workOrder.getVehicle().getWorkplace().getCompany().getId().equals(user.getCompany().getId())) {
            throw new PartsServiceException("Access denied to work order");
        }

        return workOrder;
    }

    private VehicleContext buildVehicleContext(Vehicle vehicle) {
        VehicleContext ctx = new VehicleContext();
        ctx.setRegistrationNumber(vehicle.getRegistrationNumber());
        ctx.setMake(vehicle.getVehicleModel().getBrand());
        ctx.setModel(vehicle.getVehicleModel().getModel());
        ctx.setYear(vehicle.getVehicleModel().getYear());
        return ctx;
    }

    private PartLineResponse toPartLineResponse(WorkOrderPartLine partLine) {
        PartLineResponse response = new PartLineResponse();
        response.setId(partLine.getId());
        response.setSupplierCode(partLine.getSupplierCode());
        response.setSupplierPartId(partLine.getSupplierPartId());
        response.setOemNumber(partLine.getOemNumber());
        response.setPartName(partLine.getPartName());
        response.setQuantity(partLine.getQuantity());
        response.setUnitPriceExVat(partLine.getUnitPriceExVat());
        response.setFinalUnitPriceExVat(partLine.getFinalUnitPriceExVat());
        response.setCurrency(partLine.getCurrency());
        response.setAvailabilityStatus(partLine.getAvailabilityStatus());
        response.setDeliveryEstimateDays(partLine.getDeliveryEstimateDays());
        response.setStatus(partLine.getStatus().name());
        response.setSupplierOrderReference(partLine.getSupplierOrderReference());
        response.setCreatedAt(partLine.getCreatedAt());
        return response;
    }

    // Response DTOs

    private record ErrorResponse(String message) {}
}
