package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.InvoiceDto;

import java.util.List;

public interface InvoiceService {
    List<InvoiceDto> findAll();
    
    InvoiceDto findById(Long id);
    
    InvoiceDto create(InvoiceDto invoiceDto);
    
    InvoiceDto createFromQuote(Long quoteId, InvoiceDto invoiceDto);
    
    InvoiceDto update(Long id, InvoiceDto invoiceDto);
    
    void delete(Long id);
    
    List<InvoiceDto> findByClientId(Long clientId);
    
    List<InvoiceDto> findByStatus(String status);
}

