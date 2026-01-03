package se.meltastudio.cms.parts.domain;

import jakarta.persistence.*;
import se.meltastudio.cms.model.Company;

import java.time.LocalDateTime;

/**
 * Configuration for a spare parts supplier for a specific customer.
 * Defines which suppliers are enabled, their priority, and credentials.
 */
@Entity
@Table(name = "customer_supplier_config")
public class CustomerSupplierConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "supplier_code", nullable = false, length = 50)
    private String supplierCode;

    @Column(name = "priority", nullable = false)
    private int priority;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "ordering_enabled", nullable = false)
    private boolean orderingEnabled = false;

    @Column(name = "credentials_ref", length = 500)
    private String credentialsRef;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isOrderingEnabled() {
        return orderingEnabled;
    }

    public void setOrderingEnabled(boolean orderingEnabled) {
        this.orderingEnabled = orderingEnabled;
    }

    public String getCredentialsRef() {
        return credentialsRef;
    }

    public void setCredentialsRef(String credentialsRef) {
        this.credentialsRef = credentialsRef;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
