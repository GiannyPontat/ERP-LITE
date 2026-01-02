package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepo extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByInvoiceId(Long invoiceId);
    
    void deleteByInvoiceId(Long invoiceId);
}

