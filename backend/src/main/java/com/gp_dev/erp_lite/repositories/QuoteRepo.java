package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.Quote;
import com.gp_dev.erp_lite.models.QuoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepo extends JpaRepository<Quote, Long> {
    Optional<Quote> findByQuoteNumber(String quoteNumber);
    
    List<Quote> findByClientId(Long clientId);
    
    List<Quote> findByCreatedById(Long userId);
    
    List<Quote> findByStatus(QuoteStatus status);
    
    @Query("SELECT MAX(q.quoteNumber) FROM Quote q WHERE q.quoteNumber LIKE ?1")
    Optional<String> findLastQuoteNumberByPrefix(String prefix);
}

