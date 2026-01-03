package se.meltastudio.cms.parts.supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.meltastudio.cms.parts.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MockSupplierAdapter.
 * Verifies mock data generation and search functionality.
 */
class MockSupplierAdapterTest {

    private MockSupplierAdapter adapter;
    private SupplierContext supplierContext;
    private VehicleContext vehicleContext;

    @BeforeEach
    void setUp() {
        adapter = new MockSupplierAdapter();

        supplierContext = new SupplierContext(1L, "MOCK_SUPPLIER");

        vehicleContext = new VehicleContext();
        vehicleContext.setMake("BMW");
        vehicleContext.setModel("320d");
        vehicleContext.setYear(2015);
    }

    @Test
    void getSupplierCode_ShouldReturnCorrectCode() {
        assertEquals("MOCK_SUPPLIER", adapter.getSupplierCode());
    }

    @Test
    void supportsOrdering_ShouldReturnTrue() {
        assertTrue(adapter.supportsOrdering());
    }

    @Test
    void searchParts_BrakeQuery_ShouldReturnBrakeParts() throws Exception {
        // Given
        PartSearchQuery query = new PartSearchQuery("brake pads", null, 20);

        // When
        List<PartOffer> results = adapter.searchParts(supplierContext, vehicleContext, query);

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getPartName().contains("Broms")));
    }

    @Test
    void searchParts_OilQuery_ShouldReturnOilParts() throws Exception {
        // Given
        PartSearchQuery query = new PartSearchQuery("oil", null, 20);

        // When
        List<PartOffer> results = adapter.searchParts(supplierContext, vehicleContext, query);

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getSupplierPartId().startsWith("OIL-")));
    }

    @Test
    void searchParts_FilterQuery_ShouldReturnMultipleFilters() throws Exception {
        // Given
        PartSearchQuery query = new PartSearchQuery("filter", null, 20);

        // When
        List<PartOffer> results = adapter.searchParts(supplierContext, vehicleContext, query);

        // Then
        assertNotNull(results);
        assertTrue(results.size() >= 3, "Should return multiple filter types");
        assertTrue(results.stream().anyMatch(p -> p.getPartName().contains("Oljefilter")));
        assertTrue(results.stream().anyMatch(p -> p.getPartName().contains("Luftfilter")));
    }

    @Test
    void searchParts_WithLimit_ShouldRespectLimit() throws Exception {
        // Given
        PartSearchQuery query = new PartSearchQuery("filter", null, 2);

        // When
        List<PartOffer> results = adapter.searchParts(supplierContext, vehicleContext, query);

        // Then
        assertTrue(results.size() <= 2, "Should respect limit parameter");
    }

    @Test
    void searchParts_GenericQuery_ShouldReturnGenericPart() throws Exception {
        // Given
        PartSearchQuery query = new PartSearchQuery("unknown part", null, 20);

        // When
        List<PartOffer> results = adapter.searchParts(supplierContext, vehicleContext, query);

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).getSupplierPartId().startsWith("GEN-"));
    }

    @Test
    void getPartDetails_BrakePart_ShouldReturnDetailedInfo() throws Exception {
        // Given
        String partId = "BRK-001";

        // When
        PartDetails details = adapter.getPartDetails(supplierContext, vehicleContext, partId);

        // Then
        assertNotNull(details);
        assertEquals("MOCK_SUPPLIER", details.getSupplierCode());
        assertEquals(partId, details.getSupplierPartId());
        assertEquals("Bromsbelägg fram", details.getPartName());
        assertEquals("BOSCH", details.getBrand());
        assertNotNull(details.getOemNumber());
        assertNotNull(details.getUnitPriceExVat());
        assertNotNull(details.getSpecifications());
        assertFalse(details.getSpecifications().isEmpty());
    }

    @Test
    void getPartDetails_OilPart_ShouldReturnOilDetails() throws Exception {
        // Given
        String partId = "OIL-001";

        // When
        PartDetails details = adapter.getPartDetails(supplierContext, vehicleContext, partId);

        // Then
        assertNotNull(details);
        assertEquals("Motorolja 5W-30 5L", details.getPartName());
        assertEquals("CASTROL", details.getBrand());
        assertEquals("Lubricants", details.getCategory());
        assertTrue(details.getSpecifications().stream().anyMatch(s -> s.contains("Viskositet")));
    }

    @Test
    void placeOrder_ValidRequest_ShouldReturnSuccessResult() throws Exception {
        // Given
        OrderRequest request = new OrderRequest("BRK-001", 2);

        // When
        OrderResult result = adapter.placeOrder(supplierContext, request);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getSupplierOrderReference());
        assertTrue(result.getSupplierOrderReference().startsWith("MOCK-ORD-"));
        assertNotNull(result.getEstimatedDeliveryDate());
    }

    @Test
    void searchParts_AllResults_ShouldHaveRequiredFields() throws Exception {
        // Given
        PartSearchQuery query = new PartSearchQuery("filter", null, 20);

        // When
        List<PartOffer> results = adapter.searchParts(supplierContext, vehicleContext, query);

        // Then
        for (PartOffer offer : results) {
            assertNotNull(offer.getSupplierCode());
            assertNotNull(offer.getSupplierPartId());
            assertNotNull(offer.getPartName());
            assertNotNull(offer.getBrand());
            assertNotNull(offer.getUnitPriceExVat());
            assertNotNull(offer.getVatRate());
            assertNotNull(offer.getCurrency());
            assertNotNull(offer.getAvailabilityStatus());
            assertNotNull(offer.getDeliveryEstimateDays());
        }
    }
}
