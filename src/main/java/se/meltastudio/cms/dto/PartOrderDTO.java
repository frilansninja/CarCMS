package se.meltastudio.cms.dto;

import java.time.LocalDateTime;

public class PartOrderDTO {
    private Long workOrderId;
    private Long articleId;
    private Long supplierId;
    private int quantity;
    private LocalDateTime expectedArrivalDate;
    private double purchasePrice; // Inköpspris per del
    private double sellingPrice;  // Försäljningspris per del
    private boolean received; // För att hantera leveransstatus

    // ✅ Getters & Setters
    public Long getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getExpectedArrivalDate() { return expectedArrivalDate; }
    public void setExpectedArrivalDate(LocalDateTime expectedArrivalDate) { this.expectedArrivalDate = expectedArrivalDate; }

    public double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }

    public double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }

    public boolean isReceived() { return received; }
    public void setReceived(boolean received) { this.received = received; }
}
