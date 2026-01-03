package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.meltastudio.cms.model.Invoice;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByEndCustomerId(Long endCustomerId);
    List<Invoice> findByPaid(boolean paid);
}
