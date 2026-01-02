package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.repositories.InvoiceRepo;
import com.gp_dev.erp_lite.repositories.QuoteRepo;
import com.gp_dev.erp_lite.services.NumberGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Log4j2
@RequiredArgsConstructor
@Service
public class NumberGeneratorServiceImpl implements NumberGeneratorService {

    private final QuoteRepo quoteRepo;
    private final InvoiceRepo invoiceRepo;

    @Override
    public String generateQuoteNumber() {
        int year = LocalDate.now().getYear();
        String prefix = String.format("DEV-%d-", year);
        
        // Chercher le dernier numéro avec ce préfixe
        String lastNumber = quoteRepo.findLastQuoteNumberByPrefix(prefix + "%")
                .orElse(null);
        
        int nextSequence = 1;
        if (lastNumber != null) {
            // Extraire le numéro séquentiel (les 4 derniers chiffres)
            String sequencePart = lastNumber.substring(lastNumber.length() - 4);
            try {
                nextSequence = Integer.parseInt(sequencePart) + 1;
            } catch (NumberFormatException e) {
                log.warn("Failed to parse sequence from quote number: {}", lastNumber);
                nextSequence = 1;
            }
        }
        
        return String.format("%s%04d", prefix, nextSequence);
    }

    @Override
    public String generateInvoiceNumber() {
        int year = LocalDate.now().getYear();
        String prefix = String.format("FACT-%d-", year);
        
        // Chercher le dernier numéro avec ce préfixe
        String lastNumber = invoiceRepo.findLastInvoiceNumberByPrefix(prefix + "%")
                .orElse(null);
        
        int nextSequence = 1;
        if (lastNumber != null) {
            // Extraire le numéro séquentiel (les 4 derniers chiffres)
            String sequencePart = lastNumber.substring(lastNumber.length() - 4);
            try {
                nextSequence = Integer.parseInt(sequencePart) + 1;
            } catch (NumberFormatException e) {
                log.warn("Failed to parse sequence from invoice number: {}", lastNumber);
                nextSequence = 1;
            }
        }
        
        return String.format("%s%04d", prefix, nextSequence);
    }
}

