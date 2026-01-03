package se.meltastudio.cms.parts.supplier;

import se.meltastudio.cms.parts.api.*;

import java.util.List;

/**
 * Interface for spare parts supplier adapters.
 * Each supplier integration must implement this interface.
 */
public interface SupplierAdapter {

    /**
     * Get the unique code for this supplier.
     * @return supplier code (e.g., "SUPPLIER_X", "MEKONOMEN", etc.)
     */
    String getSupplierCode();

    /**
     * Search for parts matching the query.
     *
     * @param ctx supplier context with credentials and config
     * @param vehicleContext vehicle information for fitment
     * @param query search query
     * @return list of matching parts
     * @throws SupplierException if search fails
     */
    List<PartOffer> searchParts(SupplierContext ctx, VehicleContext vehicleContext, PartSearchQuery query) throws SupplierException;

    /**
     * Get detailed information about a specific part.
     *
     * @param ctx supplier context
     * @param vehicleContext vehicle information
     * @param supplierPartId the supplier's part identifier
     * @return detailed part information
     * @throws SupplierException if fetch fails
     */
    PartDetails getPartDetails(SupplierContext ctx, VehicleContext vehicleContext, String supplierPartId) throws SupplierException;

    /**
     * Place an order for a part with the supplier.
     * Default implementation throws UnsupportedOperationException.
     *
     * @param ctx supplier context
     * @param request order request
     * @return order result with supplier reference
     * @throws SupplierException if order fails
     * @throws UnsupportedOperationException if this supplier doesn't support ordering
     */
    default OrderResult placeOrder(SupplierContext ctx, OrderRequest request) throws SupplierException {
        throw new UnsupportedOperationException("Ordering not supported by supplier: " + getSupplierCode());
    }

    /**
     * Check if this supplier supports ordering.
     * @return true if placeOrder() is implemented
     */
    default boolean supportsOrdering() {
        return false;
    }
}
