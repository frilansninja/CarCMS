package se.meltastudio.cms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PartOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    @JsonBackReference
    private Article article;

    @ManyToOne
    @JoinColumn(name = "work_order_id", nullable = false)
    @JsonBackReference
    private WorkOrder workOrder;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;  // 🔥 Nu en faktisk relation till Supplier

    @Column(nullable = false)
    private int quantity;

    private LocalDateTime orderDate;
    private LocalDateTime expectedArrivalDate;

    @Column(nullable = false)
    private boolean received = false;

    @Column(nullable = true)
    private Double purchasePrice; // Inköpspris per del

    @Column(nullable = true)
    private Double sellingPrice;  // Försäljningspris per del

    // 🔥 Dynamiskt beräknade fält (inget behov av att lagra dem i databasen)
    public double getTotalPurchaseCost() {
        return (purchasePrice != null) ? purchasePrice * quantity : 0;
    }

    public double getTotalSellingPrice() {
        return (sellingPrice != null) ? sellingPrice * quantity : 0;
    }

    // ✅ Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }

    public WorkOrder getWorkOrder() { return workOrder; }
    public void setWorkOrder(WorkOrder workOrder) { this.workOrder = workOrder; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public LocalDateTime getExpectedArrivalDate() { return expectedArrivalDate; }
    public void setExpectedArrivalDate(LocalDateTime expectedArrivalDate) { this.expectedArrivalDate = expectedArrivalDate; }

    public boolean isReceived() { return received; }
    public void setReceived(boolean received) { this.received = received; }

    public Double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(Double purchasePrice) { this.purchasePrice = purchasePrice; }

    public Double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(Double sellingPrice) { this.sellingPrice = sellingPrice; }
}
