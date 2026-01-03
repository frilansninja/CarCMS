package se.meltastudio.cms.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import se.meltastudio.cms.dto.InvoiceDTO;
import se.meltastudio.cms.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.meltastudio.cms.service.PDFGeneratorService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PDFGeneratorService pdfGeneratorService;


    public InvoiceController(InvoiceService invoiceService, PDFGeneratorService pdfGeneratorService) {
        this.invoiceService = invoiceService;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<InvoiceDTO> generateInvoice(@RequestParam Long customerId, @RequestParam BigDecimal totalAmount) {
        InvoiceDTO invoice = invoiceService.generateInvoice(customerId, totalAmount);
        return ResponseEntity.ok(invoice);
    }


    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        Optional<InvoiceDTO> invoice = invoiceService.getInvoiceById(id);
        return invoice.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<InvoiceDTO>> getUnpaidInvoices() {
        return ResponseEntity.ok(invoiceService.getUnpaidInvoices());
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<Void> markAsPaid(@PathVariable Long id) {
        invoiceService.markAsPaid(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateInvoicePDF(@PathVariable Long id) {
        Optional<InvoiceDTO> invoiceOpt = invoiceService.getInvoiceById(id);
        if (invoiceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfContent = pdfGeneratorService.generateInvoicePDF(invoiceOpt.get());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=faktura_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
}

