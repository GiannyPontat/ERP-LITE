package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.Invoice;
import com.gp_dev.erp_lite.models.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepo extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    List<Invoice> findByClientId(Long clientId);
    
    List<Invoice> findByCreatedById(Long userId);
    
    List<Invoice> findByQuoteId(Long quoteId);
    
    List<Invoice> findByStatus(InvoiceStatus status);
    
    @Query("SELECT MAX(i.invoiceNumber) FROM Invoice i WHERE i.invoiceNumber LIKE ?1")
    Optional<String> findLastInvoiceNumberByPrefix(String prefix);
}

