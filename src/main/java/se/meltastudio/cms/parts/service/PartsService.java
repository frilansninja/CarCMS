package se.meltastudio.cms.parts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.meltastudio.cms.integration.common.SimpleTtlCache;
import se.meltastudio.cms.model.Company;
import se.meltastudio.cms.model.User;
import se.meltastudio.cms.model.Vehicle;
import se.meltastudio.cms.model.WorkOrder;
import se.meltastudio.cms.parts.api.*;
import se.meltastudio.cms.parts.domain.CustomerSupplierConfig;
import se.meltastudio.cms.parts.domain.PartLineStatus;
import se.meltastudio.cms.parts.domain.WorkOrderPartLine;
import se.meltastudio.cms.parts.repository.CustomerSupplierConfigRepository;
import se.meltastudio.cms.parts.repository.WorkOrderPartLineRepository;
import se.meltastudio.cms.parts.supplier.SupplierAdapter;
import se.meltastudio.cms.parts.supplier.SupplierAdapterRegistry;
import se.meltastudio.cms.parts.supplier.SupplierException;
import se.meltastudio.cms.repository.WorkOrderRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Core service for spare parts functionality.
 * Orchestrates supplier adapters, pricing, caching, and persistence.
 */
@Service
public class PartsService {

    private static final Logger log = LoggerFactory.getLogger(PartsService.class);

    private final SupplierAdapterRegistry adapterRegistry;
    private final CustomerSupplierConfigRepository supplierConfigRepository;
    private final WorkOrderPartLineRepository partLineRepository;
    private final WorkOrderRepository workOrderRepository;
    private final ObjectMapper objectMapper;
    private final SimpleTtlCache<String, List<PartOffer>> searchCache;
    private final SupplierMetrics metrics;

    public PartsService(
            SupplierAdapterRegistry adapterRegistry,
            CustomerSupplierConfigRepository supplierConfigRepository,
            WorkOrderPartLineRepository partLineRepository,
            WorkOrderRepository workOrderRepository,
            ObjectMapper objectMapper,
            SimpleTtlCache<String, List<PartOffer>> partsSearchCache,
            SupplierMetrics metrics) {
        this.adapterRegistry = adapterRegistry;
        this.supplierConfigRepository = supplierConfigRepository;
        this.partLineRepository = partLineRepository;
        this.workOrderRepository = workOrderRepository;
        this.objectMapper = objectMapper;
        this.searchCache = partsSearchCache;
        this.metrics = metrics;
    }

    /**
     * Search for parts across enabled suppliers with fallback.
     *
     * @param company the company making the search
     * @param vehicleContext vehicle information
     * @param query search query
     * @return list of part offers from suppliers
     * @throws PartsServiceException if all suppliers fail
     */
    public List<PartOffer> searchParts(Company company, VehicleContext vehicleContext, PartSearchQuery query) throws PartsServiceException {
        long startTime = System.currentTimeMillis();
        log.info("Searching parts for company {} with query: {} [vehicle: {} {} {}]",
                company.getId(), query.getQuery(),
                vehicleContext.getMake(), vehicleContext.getModel(), vehicleContext.getYear());

        // Build cache key
        String cacheKey = buildCacheKey(company.getId(), vehicleContext, query);

        // Check cache first
        Optional<List<PartOffer>> cached = searchCache.get(cacheKey);
        if (cached.isPresent()) {
            metrics.recordCacheHit();
            long duration = System.currentTimeMillis() - startTime;
            log.info("Cache hit for parts search: {} [duration: {}ms, results: {}]",
                    cacheKey, duration, cached.get().size());
            return cached.get();
        }

        metrics.recordCacheMiss();
        log.debug("Cache miss for parts search: {}", cacheKey);

        List<CustomerSupplierConfig> suppliers = supplierConfigRepository.findEnabledSuppliersByCompanyOrderByPriority(company);

        if (suppliers.isEmpty()) {
            log.warn("No enabled suppliers found for company {}", company.getId());
            return new ArrayList<>();
        }

        List<PartOffer> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Try each supplier in priority order (fallback strategy)
        for (CustomerSupplierConfig config : suppliers) {
            Optional<SupplierAdapter> adapterOpt = adapterRegistry.getAdapter(config.getSupplierCode());

            if (adapterOpt.isEmpty()) {
                log.warn("Adapter not found for supplier code: {}", config.getSupplierCode());
                continue;
            }

            SupplierAdapter adapter = adapterOpt.get();
            SupplierContext ctx = buildSupplierContext(config);

            long supplierStartTime = System.currentTimeMillis();
            try {
                log.debug("Calling supplier {} for search [priority: {}]", config.getSupplierCode(), config.getPriority());
                List<PartOffer> offers = adapter.searchParts(ctx, vehicleContext, query);
                long supplierDuration = System.currentTimeMillis() - supplierStartTime;

                metrics.recordSupplierCall(config.getSupplierCode());

                if (offers != null && !offers.isEmpty()) {
                    results.addAll(offers);
                    log.info("Supplier {} returned {} results [duration: {}ms, status: SUCCESS]",
                            config.getSupplierCode(), offers.size(), supplierDuration);

                    // MVP: Return first non-empty result (Strategy A from PRD)
                    break;
                }

                log.debug("Supplier {} returned no results [duration: {}ms, status: EMPTY]",
                        config.getSupplierCode(), supplierDuration);

            } catch (SupplierException e) {
                long supplierDuration = System.currentTimeMillis() - supplierStartTime;
                metrics.recordSupplierFailure(config.getSupplierCode());

                String error = String.format("Supplier %s failed: %s", config.getSupplierCode(), e.getMessage());
                errors.add(error);
                log.warn("Supplier {} failed [duration: {}ms, status: FAILURE, error: {}]",
                        config.getSupplierCode(), supplierDuration, e.getMessage(), e);
                // Continue to next supplier (fallback)
            }
        }

        if (results.isEmpty() && !errors.isEmpty()) {
            long totalDuration = System.currentTimeMillis() - startTime;
            log.error("All suppliers failed [duration: {}ms]", totalDuration);
            throw new PartsServiceException("All suppliers failed: " + String.join("; ", errors));
        }

        // Store in cache before returning
        if (!results.isEmpty()) {
            searchCache.put(cacheKey, results);
            log.debug("Cached {} results for key: {}", results.size(), cacheKey);
        }

        long totalDuration = System.currentTimeMillis() - startTime;
        log.info("Parts search completed [duration: {}ms, results: {}, cache: MISS]",
                totalDuration, results.size());

        return results;
    }

    /**
     * Get detailed information about a specific part.
     */
    public PartDetails getPartDetails(Company company, String supplierCode, String supplierPartId, VehicleContext vehicleContext) throws PartsServiceException {
        log.info("Getting part details for supplier {} part {}", supplierCode, supplierPartId);

        Optional<CustomerSupplierConfig> configOpt = supplierConfigRepository.findByCompanyAndSupplierCode(company, supplierCode);
        if (configOpt.isEmpty() || !configOpt.get().isEnabled()) {
            throw new PartsServiceException("Supplier not enabled for this company: " + supplierCode);
        }

        SupplierAdapter adapter = adapterRegistry.getAdapter(supplierCode)
                .orElseThrow(() -> new PartsServiceException("Supplier adapter not found: " + supplierCode));

        SupplierContext ctx = buildSupplierContext(configOpt.get());

        try {
            return adapter.getPartDetails(ctx, vehicleContext, supplierPartId);
        } catch (SupplierException e) {
            throw new PartsServiceException("Failed to get part details: " + e.getMessage(), e);
        }
    }

    /**
     * Add a part line to a work order.
     */
    @Transactional
    public WorkOrderPartLine addPartLine(Long workOrderId, AddPartLineRequest request, User user) throws PartsServiceException {
        log.info("Adding part line to work order {} by user {}", workOrderId, user.getId());

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new PartsServiceException("Work order not found: " + workOrderId));

        // Verify work order belongs to user's company
        if (!workOrder.getVehicle().getWorkplace().getCompany().getId().equals(user.getCompany().getId())) {
            throw new PartsServiceException("Access denied to work order");
        }

        // Get part details from supplier
        Vehicle vehicle = workOrder.getVehicle();
        VehicleContext vehicleContext = buildVehicleContext(vehicle);

        PartDetails partDetails;
        try {
            partDetails = getPartDetails(
                    user.getCompany(),
                    request.getSupplierCode(),
                    request.getSupplierPartId(),
                    vehicleContext
            );
        } catch (Exception e) {
            throw new PartsServiceException("Failed to fetch part details: " + e.getMessage(), e);
        }

        // Calculate pricing with markup
        BigDecimal basePrice = partDetails.getUnitPriceExVat();
        BigDecimal finalPrice = calculateFinalPrice(basePrice, request);

        // Create part line
        WorkOrderPartLine partLine = new WorkOrderPartLine();
        partLine.setWorkOrder(workOrder);
        partLine.setSupplierCode(request.getSupplierCode());
        partLine.setSupplierPartId(request.getSupplierPartId());
        partLine.setOemNumber(partDetails.getOemNumber());
        partLine.setPartName(partDetails.getPartName());
        partLine.setQuantity(request.getQuantity());
        partLine.setUnitPriceExVat(basePrice);
        partLine.setVatRate(partDetails.getVatRate());
        partLine.setFinalUnitPriceExVat(finalPrice);
        partLine.setCurrency(partDetails.getCurrency());
        partLine.setAvailabilityStatus(partDetails.getAvailabilityStatus());
        partLine.setDeliveryEstimateDays(partDetails.getDeliveryEstimateDays());
        partLine.setStatus(PartLineStatus.PLANNED);
        partLine.setCreatedBy(user);

        // Store snapshot
        try {
            String snapshot = objectMapper.writeValueAsString(partDetails);
            partLine.setSnapshotJson(snapshot);
        } catch (Exception e) {
            log.warn("Failed to serialize part snapshot", e);
        }

        return partLineRepository.save(partLine);
    }

    /**
     * Place an order for a part line with the supplier.
     */
    @Transactional
    public WorkOrderPartLine placeOrder(Long workOrderId, Long partLineId, User user) throws PartsServiceException {
        long startTime = System.currentTimeMillis();
        log.info("Placing order for part line {} on work order {} [user: {}, company: {}]",
                partLineId, workOrderId, user.getId(), user.getCompany().getId());

        WorkOrderPartLine partLine = partLineRepository.findById(partLineId)
                .orElseThrow(() -> new PartsServiceException("Part line not found: " + partLineId));

        // Verify authorization
        if (!partLine.getWorkOrder().getId().equals(workOrderId)) {
            throw new PartsServiceException("Part line does not belong to work order");
        }

        if (!partLine.getWorkOrder().getVehicle().getWorkplace().getCompany().getId().equals(user.getCompany().getId())) {
            throw new PartsServiceException("Access denied");
        }

        if (partLine.getStatus() == PartLineStatus.ORDERED) {
            throw new PartsServiceException("Part line already ordered");
        }

        // Check if supplier supports ordering
        Optional<CustomerSupplierConfig> configOpt = supplierConfigRepository.findByCompanyAndSupplierCode(
                user.getCompany(),
                partLine.getSupplierCode()
        );

        if (configOpt.isEmpty() || !configOpt.get().isOrderingEnabled()) {
            throw new PartsServiceException("Ordering not enabled for supplier: " + partLine.getSupplierCode());
        }

        SupplierAdapter adapter = adapterRegistry.getAdapter(partLine.getSupplierCode())
                .orElseThrow(() -> new PartsServiceException("Supplier adapter not found"));

        if (!adapter.supportsOrdering()) {
            throw new PartsServiceException("Supplier does not support ordering");
        }

        // Build order request
        OrderRequest orderRequest = new OrderRequest(partLine.getSupplierPartId(), partLine.getQuantity());
        SupplierContext ctx = buildSupplierContext(configOpt.get());

        long supplierStartTime = System.currentTimeMillis();
        try {
            log.debug("Calling supplier {} to place order [part: {}, qty: {}]",
                    partLine.getSupplierCode(), partLine.getSupplierPartId(), partLine.getQuantity());

            OrderResult result = adapter.placeOrder(ctx, orderRequest);
            long supplierDuration = System.currentTimeMillis() - supplierStartTime;

            if (result.isSuccess()) {
                metrics.recordSupplierCall(partLine.getSupplierCode());
                partLine.setStatus(PartLineStatus.ORDERED);
                partLine.setSupplierOrderReference(result.getSupplierOrderReference());

                long totalDuration = System.currentTimeMillis() - startTime;
                log.info("Order placed successfully [supplier: {}, ref: {}, duration: {}ms, supplier-duration: {}ms]",
                        partLine.getSupplierCode(), result.getSupplierOrderReference(),
                        totalDuration, supplierDuration);

                return partLineRepository.save(partLine);
            } else {
                metrics.recordSupplierFailure(partLine.getSupplierCode());
                log.warn("Order failed [supplier: {}, duration: {}ms, message: {}]",
                        partLine.getSupplierCode(), supplierDuration, result.getMessage());
                throw new PartsServiceException("Order failed: " + result.getMessage());
            }

        } catch (SupplierException e) {
            long supplierDuration = System.currentTimeMillis() - supplierStartTime;
            metrics.recordSupplierFailure(partLine.getSupplierCode());
            log.error("Supplier error placing order [supplier: {}, duration: {}ms, error: {}]",
                    partLine.getSupplierCode(), supplierDuration, e.getMessage(), e);
            throw new PartsServiceException("Failed to place order: " + e.getMessage(), e);
        }
    }

    /**
     * Get all part lines for a work order.
     */
    public List<WorkOrderPartLine> getWorkOrderPartLines(Long workOrderId, User user) throws PartsServiceException {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new PartsServiceException("Work order not found"));

        // Verify access
        if (!workOrder.getVehicle().getWorkplace().getCompany().getId().equals(user.getCompany().getId())) {
            throw new PartsServiceException("Access denied");
        }

        return partLineRepository.findByWorkOrderOrderByCreatedAtDesc(workOrder);
    }

    /**
     * Cancel a part line.
     */
    @Transactional
    public void cancelPartLine(Long workOrderId, Long partLineId, User user) throws PartsServiceException {
        WorkOrderPartLine partLine = partLineRepository.findById(partLineId)
                .orElseThrow(() -> new PartsServiceException("Part line not found"));

        // Verify authorization
        if (!partLine.getWorkOrder().getId().equals(workOrderId)) {
            throw new PartsServiceException("Part line does not belong to work order");
        }

        if (!partLine.getWorkOrder().getVehicle().getWorkplace().getCompany().getId().equals(user.getCompany().getId())) {
            throw new PartsServiceException("Access denied");
        }

        partLine.setStatus(PartLineStatus.CANCELLED);
        partLineRepository.save(partLine);
    }

    // Helper methods

    private SupplierContext buildSupplierContext(CustomerSupplierConfig config) {
        SupplierContext ctx = new SupplierContext(config.getCompany().getId(), config.getSupplierCode());
        // TODO: Load credentials from credentialsRef (could be encrypted, external vault, etc.)
        // For MVP, we'll handle this later
        return ctx;
    }

    private VehicleContext buildVehicleContext(Vehicle vehicle) {
        VehicleContext ctx = new VehicleContext();
        ctx.setRegistrationNumber(vehicle.getRegistrationNumber());
        // VIN not yet in Vehicle model - can be added later if needed
        ctx.setMake(vehicle.getVehicleModel().getBrand());
        ctx.setModel(vehicle.getVehicleModel().getModel());
        ctx.setYear(vehicle.getVehicleModel().getYear());
        return ctx;
    }

    private BigDecimal calculateFinalPrice(BigDecimal basePrice, AddPartLineRequest request) {
        if (request.getManualMarkupPercent() != null) {
            BigDecimal markup = basePrice.multiply(request.getManualMarkupPercent()).divide(BigDecimal.valueOf(100));
            return basePrice.add(markup);
        } else if (request.getManualMarkupFixed() != null) {
            return basePrice.add(request.getManualMarkupFixed());
        }
        // No markup
        return basePrice;
    }

    private String buildCacheKey(Long companyId, VehicleContext vehicleContext, PartSearchQuery query) {
        // Build stable cache key from search parameters
        StringBuilder key = new StringBuilder();
        key.append(companyId).append(":");

        // Vehicle identity
        if (vehicleContext.getRegistrationNumber() != null) {
            key.append(vehicleContext.getRegistrationNumber()).append(":");
        }
        if (vehicleContext.getMake() != null) {
            key.append(vehicleContext.getMake()).append(":");
        }
        if (vehicleContext.getModel() != null) {
            key.append(vehicleContext.getModel()).append(":");
        }
        if (vehicleContext.getYear() != null) {
            key.append(vehicleContext.getYear()).append(":");
        }

        // Search parameters
        key.append(query.getQuery().toLowerCase()).append(":");
        if (query.getCategory() != null) {
            key.append(query.getCategory().toLowerCase()).append(":");
        }
        key.append(query.getLimit());

        return key.toString();
    }
}
