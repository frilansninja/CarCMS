package se.meltastudio.cms.parts.domain;

import jakarta.persistence.*;
import se.meltastudio.cms.model.User;
import se.meltastudio.cms.model.WorkOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a spare part line item attached to a work order.
 * Contains immutable snapshot of supplier data for audit trail.
 */
@Entity
@Table(name = "work_order_part_line")
public class WorkOrderPartLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @Column(name = "supplier_code", nullable = false, length = 50)
    private String supplierCode;

    @Column(name = "supplier_part_id", nullable = false, length = 100)
    private String supplierPartId;

    @Column(name = "oem_number", length = 100)
    private String oemNumber;

    @Column(name = "part_name", nullable = false, length = 255)
    private String partName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price_ex_vat", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPriceExVat;

    @Column(name = "vat_rate", precision = 5, scale = 2)
    private BigDecimal vatRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "markup_type", length = 20)
    private MarkupType markupType = MarkupType.NONE;

    @Column(name = "markup_value", precision = 10, scale = 2)
    private BigDecimal markupValue;

    @Column(name = "final_unit_price_ex_vat", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalUnitPriceExVat;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "SEK";

    @Column(name = "availability_status", length = 50)
    private String availabilityStatus;

    @Column(name = "delivery_estimate_days")
    private Integer deliveryEstimateDays;

    @Column(name = "snapshot_json", columnDefinition = "TEXT")
    private String snapshotJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PartLineStatus status = PartLineStatus.PLANNED;

    @Column(name = "supplier_order_reference", length = 100)
    private String supplierOrderReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

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

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierPartId() {
        return supplierPartId;
    }

    public void setSupplierPartId(String supplierPartId) {
        this.supplierPartId = supplierPartId;
    }

    public String getOemNumber() {
        return oemNumber;
    }

    public void setOemNumber(String oemNumber) {
        this.oemNumber = oemNumber;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPriceExVat() {
        return unitPriceExVat;
    }

    public void setUnitPriceExVat(BigDecimal unitPriceExVat) {
        this.unitPriceExVat = unitPriceExVat;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public MarkupType getMarkupType() {
        return markupType;
    }

    public void setMarkupType(MarkupType markupType) {
        this.markupType = markupType;
    }

    public BigDecimal getMarkupValue() {
        return markupValue;
    }

    public void setMarkupValue(BigDecimal markupValue) {
        this.markupValue = markupValue;
    }

    public BigDecimal getFinalUnitPriceExVat() {
        return finalUnitPriceExVat;
    }

    public void setFinalUnitPriceExVat(BigDecimal finalUnitPriceExVat) {
        this.finalUnitPriceExVat = finalUnitPriceExVat;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Integer getDeliveryEstimateDays() {
        return deliveryEstimateDays;
    }

    public void setDeliveryEstimateDays(Integer deliveryEstimateDays) {
        this.deliveryEstimateDays = deliveryEstimateDays;
    }

    public String getSnapshotJson() {
        return snapshotJson;
    }

    public void setSnapshotJson(String snapshotJson) {
        this.snapshotJson = snapshotJson;
    }

    public PartLineStatus getStatus() {
        return status;
    }

    public void setStatus(PartLineStatus status) {
        this.status = status;
    }

    public String getSupplierOrderReference() {
        return supplierOrderReference;
    }

    public void setSupplierOrderReference(String supplierOrderReference) {
        this.supplierOrderReference = supplierOrderReference;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
