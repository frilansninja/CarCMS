package se.meltastudio.cms.parts.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for work order part lines.
 */
public class PartLineResponse {

    private Long id;
    private String supplierCode;
    private String supplierPartId;
    private String oemNumber;
    private String partName;
    private int quantity;
    private BigDecimal unitPriceExVat;
    private BigDecimal finalUnitPriceExVat;
    private String currency;
    private String availabilityStatus;
    private Integer deliveryEstimateDays;
    private String status;
    private String supplierOrderReference;
    private LocalDateTime createdAt;

    public PartLineResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSupplierOrderReference() {
        return supplierOrderReference;
    }

    public void setSupplierOrderReference(String supplierOrderReference) {
        this.supplierOrderReference = supplierOrderReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
