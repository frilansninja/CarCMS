package se.meltastudio.cms.parts.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Context information for supplier adapter calls.
 * Contains credentials and configuration for a specific supplier.
 */
public class SupplierContext {

    private Long customerId;
    private String supplierCode;
    private Map<String, String> credentials;
    private Map<String, Object> config;

    public SupplierContext() {
        this.credentials = new HashMap<>();
        this.config = new HashMap<>();
    }

    public SupplierContext(Long customerId, String supplierCode) {
        this();
        this.customerId = customerId;
        this.supplierCode = supplierCode;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    public void addCredential(String key, String value) {
        this.credentials.put(key, value);
    }

    public String getCredential(String key) {
        return this.credentials.get(key);
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public void addConfig(String key, Object value) {
        this.config.put(key, value);
    }

    public Object getConfigValue(String key) {
        return this.config.get(key);
    }
}
