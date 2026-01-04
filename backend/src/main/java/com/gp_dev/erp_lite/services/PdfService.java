package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.InvoiceDto;
import com.gp_dev.erp_lite.dtos.QuoteDto;

/**
 * Service pour la génération de documents PDF
 */
public interface PdfService {
    
    /**
     * Génère un PDF pour un devis
     * @param quoteDto Le devis à convertir en PDF
     * @return Tableau de bytes représentant le PDF
     */
    byte[] generateQuotePdf(QuoteDto quoteDto);
    
    /**
     * Génère un PDF pour une facture
     * @param invoiceDto La facture à convertir en PDF
     * @return Tableau de bytes représentant le PDF
     */
    byte[] generateInvoicePdf(InvoiceDto invoiceDto);
}

