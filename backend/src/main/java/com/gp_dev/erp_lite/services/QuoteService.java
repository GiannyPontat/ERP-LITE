package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.QuoteDto;

import java.util.List;

public interface QuoteService {
    List<QuoteDto> findAll();
    
    QuoteDto findById(Long id);
    
    QuoteDto create(QuoteDto quoteDto);
    
    QuoteDto update(Long id, QuoteDto quoteDto);
    
    void delete(Long id);
    
    List<QuoteDto> findByClientId(Long clientId);
    
    List<QuoteDto> findByStatus(String status);
}

