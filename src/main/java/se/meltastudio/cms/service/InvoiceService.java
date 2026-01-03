package se.meltastudio.cms.service;

import org.springframework.stereotype.Service;
import se.meltastudio.cms.dto.InvoiceDTO;
import se.meltastudio.cms.model.EndCustomer;
import se.meltastudio.cms.model.Invoice;
import se.meltastudio.cms.repository.EndCustomerRepository;
import se.meltastudio.cms.repository.InvoiceRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final EndCustomerRepository endCustomerRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, EndCustomerRepository endCustomerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.endCustomerRepository = endCustomerRepository;
    }


    public InvoiceDTO generateInvoice(Long customerId, BigDecimal totalAmount) {
        Optional<EndCustomer> customerOpt = endCustomerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found");
        }
        EndCustomer endCustomer = customerOpt.get();

        String invoiceNumber = "INV-" + UUID.randomUUID().toString().substring(0, 8);
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(30);

        Invoice invoice = new Invoice(invoiceNumber, issueDate, dueDate,totalAmount, false, endCustomer);
        return toDTO(invoiceRepository.save(invoice));
    }

    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<InvoiceDTO> getUnpaidInvoices() {
        return invoiceRepository.findByPaid(false).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<InvoiceDTO> getInvoiceById(Long id) {
        return invoiceRepository.findById(id).map(this::toDTO);
    }

    public void markAsPaid(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        invoice.setPaid(true);
        invoiceRepository.save(invoice);
    }


    public InvoiceDTO toDTO(Invoice invoice) {

        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setAmount(invoice.getAmount());
        dto.setDueDate(invoice.getDueDate());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setPaid(invoice.isPaid());
        dto.setEndCustomerId(invoice.getEndCustomer().getId());
        dto.setIssueDate(invoice.getIssueDate());
        return dto;
    }


}
