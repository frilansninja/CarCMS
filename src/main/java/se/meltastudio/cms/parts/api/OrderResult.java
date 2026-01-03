package se.meltastudio.cms.parts.api;

import java.time.LocalDateTime;

/**
 * Result of placing an order with a supplier.
 */
public class OrderResult {

    private boolean success;
    private String supplierOrderReference;
    private String message;
    private LocalDateTime estimatedDeliveryDate;
    private String trackingUrl;

    public OrderResult() {
    }

    public OrderResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSupplierOrderReference() {
        return supplierOrderReference;
    }

    public void setSupplierOrderReference(String supplierOrderReference) {
        this.supplierOrderReference = supplierOrderReference;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }
}
