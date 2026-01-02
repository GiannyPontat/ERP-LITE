package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.QuoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteItemRepo extends JpaRepository<QuoteItem, Long> {
    List<QuoteItem> findByQuoteId(Long quoteId);
    
    void deleteByQuoteId(Long quoteId);
}

