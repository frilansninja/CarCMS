package se.meltastudio.cms.parts.api;

import java.math.BigDecimal;

/**
 * Request to add a part line to a work order.
 */
public class AddPartLineRequest {

    private String supplierCode;
    private String supplierPartId;
    private int quantity;
    private BigDecimal manualMarkupPercent;
    private BigDecimal manualMarkupFixed;
    private String notes;

    public AddPartLineRequest() {
    }

    public AddPartLineRequest(String supplierCode, String supplierPartId, int quantity) {
        this.supplierCode = supplierCode;
        this.supplierPartId = supplierPartId;
        this.quantity = quantity;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getManualMarkupPercent() {
        return manualMarkupPercent;
    }

    public void setManualMarkupPercent(BigDecimal manualMarkupPercent) {
        this.manualMarkupPercent = manualMarkupPercent;
    }

    public BigDecimal getManualMarkupFixed() {
        return manualMarkupFixed;
    }

    public void setManualMarkupFixed(BigDecimal manualMarkupFixed) {
        this.manualMarkupFixed = manualMarkupFixed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
