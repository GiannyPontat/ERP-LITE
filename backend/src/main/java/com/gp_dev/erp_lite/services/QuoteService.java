package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.InvoiceDto;
import com.gp_dev.erp_lite.dtos.QuoteDto;
import com.gp_dev.erp_lite.models.QuoteStatus;

import java.util.List;

public interface QuoteService {
    List<QuoteDto> findAll();

    QuoteDto findById(Long id);

    QuoteDto create(QuoteDto quoteDto);

    QuoteDto update(Long id, QuoteDto quoteDto);

    void delete(Long id);

    List<QuoteDto> findByClientId(Long clientId);

    List<QuoteDto> findByStatus(String status);

    void updateStatus(Long quoteId, QuoteStatus newStatus);

    InvoiceDto convertToInvoice(Long quoteId);
}

