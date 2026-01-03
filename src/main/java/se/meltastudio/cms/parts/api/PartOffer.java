package se.meltastudio.cms.parts.api;

import java.math.BigDecimal;

/**
 * Represents a single part offer from a supplier.
 * Normalized format used across all suppliers.
 */
public class PartOffer {

    private String supplierCode;
    private String supplierPartId;
    private String oemNumber;
    private String partName;
    private String description;
    private String category;
    private BigDecimal unitPriceExVat;
    private BigDecimal vatRate;
    private String currency;
    private String availabilityStatus;
    private Integer deliveryEstimateDays;
    private String imageUrl;
    private String brand;

    public PartOffer() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
