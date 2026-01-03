package se.meltastudio.cms.parts.supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.meltastudio.cms.parts.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Mock supplier adapter for testing and development.
 * Returns realistic test data without external dependencies.
 */
@Component
public class MockSupplierAdapter implements SupplierAdapter {

    private static final Logger log = LoggerFactory.getLogger(MockSupplierAdapter.class);
    private static final String SUPPLIER_CODE = "MOCK_SUPPLIER";

    @Override
    public String getSupplierCode() {
        return SUPPLIER_CODE;
    }

    @Override
    public List<PartOffer> searchParts(SupplierContext ctx, VehicleContext vehicleContext, PartSearchQuery query) throws SupplierException {
        log.info("Mock supplier searching for: {} (vehicle: {} {} {})",
                query.getQuery(), vehicleContext.getMake(), vehicleContext.getModel(), vehicleContext.getYear());

        List<PartOffer> results = new ArrayList<>();
        String searchTerm = query.getQuery().toLowerCase();

        // Simulate search results based on query
        if (searchTerm.contains("brake") || searchTerm.contains("broms")) {
            results.add(createPartOffer("BRK-001", "Bromsbelägg fram", "BOSCH", 450.00, "IN_STOCK", 1));
            results.add(createPartOffer("BRK-002", "Bromsbelägg bak", "BREMBO", 380.00, "IN_STOCK", 1));
            results.add(createPartOffer("BRK-003", "Bromsskivor fram (par)", "ATE", 890.00, "LOW_STOCK", 2));
        } else if (searchTerm.contains("oil") || searchTerm.contains("olja")) {
            results.add(createPartOffer("OIL-001", "Motorolja 5W-30 5L", "CASTROL", 320.00, "IN_STOCK", 0));
            results.add(createPartOffer("OIL-002", "Oljefilter", "MANN", 85.00, "IN_STOCK", 1));
        } else if (searchTerm.contains("filter")) {
            results.add(createPartOffer("FLT-001", "Oljefilter", "MANN", 85.00, "IN_STOCK", 1));
            results.add(createPartOffer("FLT-002", "Luftfilter", "MAHLE", 125.00, "IN_STOCK", 1));
            results.add(createPartOffer("FLT-003", "Bränslefilter", "BOSCH", 195.00, "IN_STOCK", 2));
            results.add(createPartOffer("FLT-004", "Kabinfilter", "MANN", 145.00, "IN_STOCK", 1));
        } else if (searchTerm.contains("spark") || searchTerm.contains("tändstift")) {
            results.add(createPartOffer("SPK-001", "Tändstift (4-pack)", "NGK", 280.00, "IN_STOCK", 1));
        } else {
            // Generic results for any other search
            results.add(createPartOffer("GEN-001", "Generic Part " + searchTerm, "GENERIC", 199.00, "IN_STOCK", 2));
        }

        // Apply limit
        if (results.size() > query.getLimit()) {
            results = results.subList(0, query.getLimit());
        }

        log.info("Mock supplier returning {} results", results.size());
        return results;
    }

    @Override
    public PartDetails getPartDetails(SupplierContext ctx, VehicleContext vehicleContext, String supplierPartId) throws SupplierException {
        log.info("Mock supplier getting details for part: {}", supplierPartId);

        PartDetails details = new PartDetails();
        details.setSupplierCode(SUPPLIER_CODE);
        details.setSupplierPartId(supplierPartId);

        // Simulate different parts based on ID prefix
        if (supplierPartId.startsWith("BRK-")) {
            details.setPartName("Bromsbelägg fram");
            details.setBrand("BOSCH");
            details.setOemNumber("0 986 494 123");
            details.setUnitPriceExVat(new BigDecimal("450.00"));
            details.setVatRate(new BigDecimal("25"));
            details.setCurrency("SEK");
            details.setAvailabilityStatus("IN_STOCK");
            details.setDeliveryEstimateDays(1);
            details.setCategory("Brakes");
            details.setDescription("Premium bromsbelägg för personbilar. Låg dammbildning och utmärkt bromsprestanda.");
            details.setManufacturerPartNumber("BP1234");
            details.setWeight("2.5 kg");
            details.setDimensions("150x80x20 mm");
            details.setSpecifications(List.of(
                    "Material: Keramisk",
                    "Friktion: Hög",
                    "Temperaturområde: -40°C till +300°C",
                    "Livslängd: 60,000 km"
            ));
        } else if (supplierPartId.startsWith("OIL-")) {
            details.setPartName("Motorolja 5W-30 5L");
            details.setBrand("CASTROL");
            details.setOemNumber("EDGE-5W30-5L");
            details.setUnitPriceExVat(new BigDecimal("320.00"));
            details.setVatRate(new BigDecimal("25"));
            details.setCurrency("SEK");
            details.setAvailabilityStatus("IN_STOCK");
            details.setDeliveryEstimateDays(0);
            details.setCategory("Lubricants");
            details.setDescription("Syntetisk motorolja för moderna bensin- och dieselmotorer.");
            details.setWeight("4.5 kg");
            details.setSpecifications(List.of(
                    "Viskositet: 5W-30",
                    "Specifikation: ACEA C3",
                    "API: SN/CF",
                    "Volym: 5 liter"
            ));
        } else if (supplierPartId.startsWith("FLT-")) {
            details.setPartName("Oljefilter");
            details.setBrand("MANN");
            details.setOemNumber("W 712/75");
            details.setUnitPriceExVat(new BigDecimal("85.00"));
            details.setVatRate(new BigDecimal("25"));
            details.setCurrency("SEK");
            details.setAvailabilityStatus("IN_STOCK");
            details.setDeliveryEstimateDays(1);
            details.setCategory("Filters");
            details.setDescription("Högkvalitativt oljefilter för effektiv rening.");
            details.setWeight("0.3 kg");
            details.setSpecifications(List.of(
                    "Filtreringsgrad: 99%",
                    "Genomströmning: 15 l/min",
                    "Drifttryck max: 10 bar"
            ));
        } else {
            // Generic part
            details.setPartName("Generic Part " + supplierPartId);
            details.setBrand("GENERIC");
            details.setUnitPriceExVat(new BigDecimal("199.00"));
            details.setVatRate(new BigDecimal("25"));
            details.setCurrency("SEK");
            details.setAvailabilityStatus("IN_STOCK");
            details.setDeliveryEstimateDays(2);
        }

        return details;
    }

    @Override
    public OrderResult placeOrder(SupplierContext ctx, OrderRequest request) throws SupplierException {
        log.info("Mock supplier placing order for part: {} (qty: {})",
                request.getSupplierPartId(), request.getQuantity());

        // Simulate successful order
        OrderResult result = new OrderResult();
        result.setSuccess(true);
        result.setSupplierOrderReference("MOCK-ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        result.setMessage("Order placed successfully with mock supplier");
        result.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(2));

        log.info("Mock order created: {}", result.getSupplierOrderReference());
        return result;
    }

    @Override
    public boolean supportsOrdering() {
        return true;
    }

    // Helper method to create part offers
    private PartOffer createPartOffer(String partId, String name, String brand, double price, String availability, int deliveryDays) {
        PartOffer offer = new PartOffer();
        offer.setSupplierCode(SUPPLIER_CODE);
        offer.setSupplierPartId(partId);
        offer.setPartName(name);
        offer.setBrand(brand);
        offer.setUnitPriceExVat(new BigDecimal(price));
        offer.setVatRate(new BigDecimal("25"));
        offer.setCurrency("SEK");
        offer.setAvailabilityStatus(availability);
        offer.setDeliveryEstimateDays(deliveryDays);
        return offer;
    }
}
