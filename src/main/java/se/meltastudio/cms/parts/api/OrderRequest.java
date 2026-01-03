package se.meltastudio.cms.parts.api;

/**
 * Request to place an order for a part with a supplier.
 */
public class OrderRequest {

    private String supplierPartId;
    private int quantity;
    private String deliveryAddress;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String notes;

    public OrderRequest() {
    }

    public OrderRequest(String supplierPartId, int quantity) {
        this.supplierPartId = supplierPartId;
        this.quantity = quantity;
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

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
