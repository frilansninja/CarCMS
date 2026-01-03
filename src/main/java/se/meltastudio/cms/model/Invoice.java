package se.meltastudio.cms.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal amount;
    private boolean paid;

    @ManyToOne
    @JoinColumn(name = "end_customer_id", nullable = false)
    private EndCustomer endCustomer;

    public Invoice() {
    }

    public Invoice(String invoiceNumber, LocalDate issueDate, LocalDate dueDate, BigDecimal amount, boolean paid, EndCustomer endCustomer) {
        this.invoiceNumber = invoiceNumber;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.amount = amount;
        this.paid = paid;
        this.endCustomer = endCustomer;
    }

    // 🔹 Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public EndCustomer getEndCustomer() { return endCustomer; }
    public void setEndCustomer(EndCustomer endCustomer) { this.endCustomer = endCustomer; }
}
