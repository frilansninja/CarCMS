package se.meltastudio.cms.parts.supplier;

/**
 * Exception thrown when a supplier adapter encounters an error.
 */
public class SupplierException extends Exception {

    private final String supplierCode;
    private final String errorCode;

    public SupplierException(String supplierCode, String message) {
        super(message);
        this.supplierCode = supplierCode;
        this.errorCode = "UNKNOWN";
    }

    public SupplierException(String supplierCode, String errorCode, String message) {
        super(message);
        this.supplierCode = supplierCode;
        this.errorCode = errorCode;
    }

    public SupplierException(String supplierCode, String message, Throwable cause) {
        super(message, cause);
        this.supplierCode = supplierCode;
        this.errorCode = "UNKNOWN";
    }

    public SupplierException(String supplierCode, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.supplierCode = supplierCode;
        this.errorCode = errorCode;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
