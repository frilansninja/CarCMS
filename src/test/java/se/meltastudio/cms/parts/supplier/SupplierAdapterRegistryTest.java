package se.meltastudio.cms.parts.supplier;

import org.junit.jupiter.api.Test;
import se.meltastudio.cms.parts.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SupplierAdapterRegistry.
 * Verifies adapter registration and retrieval.
 */
class SupplierAdapterRegistryTest {

    @Test
    void constructor_WithMultipleAdapters_ShouldRegisterAll() {
        // Given
        SupplierAdapter adapter1 = new TestAdapter("ADAPTER_1");
        SupplierAdapter adapter2 = new TestAdapter("ADAPTER_2");
        List<SupplierAdapter> adapters = List.of(adapter1, adapter2);

        // When
        SupplierAdapterRegistry registry = new SupplierAdapterRegistry(adapters);

        // Then
        assertTrue(registry.getAdapter("ADAPTER_1").isPresent());
        assertTrue(registry.getAdapter("ADAPTER_2").isPresent());
    }

    @Test
    void getAdapter_RegisteredCode_ShouldReturnAdapter() {
        // Given
        SupplierAdapter adapter = new TestAdapter("TEST_SUPPLIER");
        SupplierAdapterRegistry registry = new SupplierAdapterRegistry(List.of(adapter));

        // When
        Optional<SupplierAdapter> result = registry.getAdapter("TEST_SUPPLIER");

        // Then
        assertTrue(result.isPresent());
        assertEquals("TEST_SUPPLIER", result.get().getSupplierCode());
    }

    @Test
    void getAdapter_UnregisteredCode_ShouldReturnEmpty() {
        // Given
        SupplierAdapterRegistry registry = new SupplierAdapterRegistry(List.of());

        // When
        Optional<SupplierAdapter> result = registry.getAdapter("UNKNOWN");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void constructor_EmptyList_ShouldCreateEmptyRegistry() {
        // Given & When
        SupplierAdapterRegistry registry = new SupplierAdapterRegistry(List.of());

        // Then
        assertFalse(registry.getAdapter("ANY").isPresent());
    }

    @Test
    void getAdapter_CaseSensitive_ShouldMatchExactly() {
        // Given
        SupplierAdapter adapter = new TestAdapter("TestSupplier");
        SupplierAdapterRegistry registry = new SupplierAdapterRegistry(List.of(adapter));

        // When & Then
        assertTrue(registry.getAdapter("TestSupplier").isPresent());
        assertFalse(registry.getAdapter("testsupplier").isPresent());
        assertFalse(registry.getAdapter("TESTSUPPLIER").isPresent());
    }

    @Test
    void getAllAdapters_ShouldReturnAllRegisteredAdapters() {
        // Given
        SupplierAdapter adapter1 = new TestAdapter("ADAPTER_1");
        SupplierAdapter adapter2 = new TestAdapter("ADAPTER_2");
        SupplierAdapter adapter3 = new TestAdapter("ADAPTER_3");
        SupplierAdapterRegistry registry = new SupplierAdapterRegistry(List.of(adapter1, adapter2, adapter3));

        // When & Then
        assertTrue(registry.getAdapter("ADAPTER_1").isPresent());
        assertTrue(registry.getAdapter("ADAPTER_2").isPresent());
        assertTrue(registry.getAdapter("ADAPTER_3").isPresent());
    }

    // Test adapter implementation
    private static class TestAdapter implements SupplierAdapter {
        private final String code;

        TestAdapter(String code) {
            this.code = code;
        }

        @Override
        public String getSupplierCode() {
            return code;
        }

        @Override
        public List<PartOffer> searchParts(SupplierContext ctx, VehicleContext vehicleContext, PartSearchQuery query) {
            return List.of();
        }

        @Override
        public PartDetails getPartDetails(SupplierContext ctx, VehicleContext vehicleContext, String supplierPartId) {
            return new PartDetails();
        }
    }
}
