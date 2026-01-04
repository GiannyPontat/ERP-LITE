package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.models.Invoice;
import com.gp_dev.erp_lite.models.InvoiceStatus;
import com.gp_dev.erp_lite.models.Quote;
import com.gp_dev.erp_lite.models.QuoteStatus;
import com.gp_dev.erp_lite.repositories.InvoiceRepo;
import com.gp_dev.erp_lite.repositories.QuoteRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ScheduledTasksService {

    private final QuoteRepo quoteRepo;
    private final InvoiceRepo invoiceRepo;

    /**
     * Marque les devis expirés tous les jours à minuit
     * S'exécute tous les jours à 00:00
     */
    @Scheduled(cron = "0 0 0 * * *") // Tous les jours à 00h00
    @Transactional
    public void markExpiredQuotes() {
        log.info("Scheduled task: Checking for expired quotes...");

        LocalDate today = LocalDate.now();

        List<Quote> quotes = quoteRepo.findByStatus(QuoteStatus.SENT);

        int expiredCount = 0;
        for (Quote quote : quotes) {
            if (quote.getValidUntil() != null && quote.getValidUntil().isBefore(today)) {
                quote.setStatus(QuoteStatus.EXPIRED);
                quoteRepo.save(quote);
                expiredCount++;
                log.info("Quote {} marked as expired (valid until: {})",
                        quote.getQuoteNumber(), quote.getValidUntil());
            }
        }

        log.info("Scheduled task completed: {} quotes marked as expired", expiredCount);
    }

    /**
     * Marque les factures en retard tous les jours à minuit
     * S'exécute tous les jours à 00:00
     */
    @Scheduled(cron = "0 0 0 * * *") // Tous les jours à 00h00
    @Transactional
    public void markOverdueInvoices() {
        log.info("Scheduled task: Checking for overdue invoices...");

        LocalDate today = LocalDate.now();

        List<Invoice> sentInvoices = invoiceRepo.findByStatus(InvoiceStatus.SENT);
        List<Invoice> partiallyPaidInvoices = invoiceRepo.findByStatus(InvoiceStatus.PARTIALLY_PAID);

        int overdueCount = 0;

        // Vérifier les factures envoyées
        for (Invoice invoice : sentInvoices) {
            if (invoice.getDueDate() != null && invoice.getDueDate().isBefore(today)) {
                invoice.setStatus(InvoiceStatus.OVERDUE);
                invoiceRepo.save(invoice);
                overdueCount++;
                log.info("Invoice {} marked as overdue (due date: {})",
                        invoice.getInvoiceNumber(), invoice.getDueDate());
            }
        }

        // Vérifier les factures partiellement payées
        for (Invoice invoice : partiallyPaidInvoices) {
            if (invoice.getDueDate() != null && invoice.getDueDate().isBefore(today)) {
                invoice.setStatus(InvoiceStatus.OVERDUE);
                invoiceRepo.save(invoice);
                overdueCount++;
                log.info("Invoice {} marked as overdue (due date: {})",
                        invoice.getInvoiceNumber(), invoice.getDueDate());
            }
        }

        log.info("Scheduled task completed: {} invoices marked as overdue", overdueCount);
    }
}
