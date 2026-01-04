package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.InvoiceDto;
import com.gp_dev.erp_lite.dtos.QuoteDto;

public interface EmailService {
    void sendVerificationEmail(String to, String token);
    void sendPasswordResetEmail(String to, String token);
    void sendPasswordChangeConfirmation(String to);

    /**
     * Envoie un devis par email au client
     *
     * @param quoteDto Le devis à envoyer
     * @param recipientEmail L'adresse email du destinataire
     */
    void sendQuoteEmail(QuoteDto quoteDto, String recipientEmail);

    /**
     * Envoie une facture par email au client
     *
     * @param invoiceDto La facture à envoyer
     * @param recipientEmail L'adresse email du destinataire
     */
    void sendInvoiceEmail(InvoiceDto invoiceDto, String recipientEmail);
}
