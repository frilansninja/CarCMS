package se.meltastudio.cms.parts.supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registry for all supplier adapters.
 * Automatically discovers and indexes all SupplierAdapter beans.
 */
@Component
public class SupplierAdapterRegistry {

    private static final Logger log = LoggerFactory.getLogger(SupplierAdapterRegistry.class);

    private final Map<String, SupplierAdapter> adapters = new HashMap<>();

    /**
     * Constructor that auto-wires all SupplierAdapter implementations.
     * @param adapters list of all SupplierAdapter beans
     */
    public SupplierAdapterRegistry(List<SupplierAdapter> adapters) {
        for (SupplierAdapter adapter : adapters) {
            String code = adapter.getSupplierCode();
            this.adapters.put(code, adapter);
            log.info("Registered supplier adapter: {} (supports ordering: {})",
                    code, adapter.supportsOrdering());
        }
        log.info("Supplier adapter registry initialized with {} adapters", this.adapters.size());
    }

    /**
     * Get adapter by supplier code.
     * @param supplierCode the supplier code
     * @return optional adapter
     */
    public Optional<SupplierAdapter> getAdapter(String supplierCode) {
        return Optional.ofNullable(adapters.get(supplierCode));
    }

    /**
     * Check if an adapter exists for the given supplier code.
     * @param supplierCode the supplier code
     * @return true if adapter exists
     */
    public boolean hasAdapter(String supplierCode) {
        return adapters.containsKey(supplierCode);
    }

    /**
     * Get all registered supplier codes.
     * @return set of supplier codes
     */
    public java.util.Set<String> getAvailableSuppliers() {
        return adapters.keySet();
    }

    /**
     * Check if a supplier supports ordering.
     * @param supplierCode the supplier code
     * @return true if supplier supports ordering
     */
    public boolean supportsOrdering(String supplierCode) {
        return getAdapter(supplierCode)
                .map(SupplierAdapter::supportsOrdering)
                .orElse(false);
    }
}
