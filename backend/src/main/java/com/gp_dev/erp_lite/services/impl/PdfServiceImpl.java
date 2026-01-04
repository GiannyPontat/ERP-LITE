package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.ClientDto;
import com.gp_dev.erp_lite.dtos.InvoiceDto;
import com.gp_dev.erp_lite.dtos.InvoiceItemDto;
import com.gp_dev.erp_lite.dtos.QuoteDto;
import com.gp_dev.erp_lite.dtos.QuoteItemDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.services.ClientService;
import com.gp_dev.erp_lite.services.PdfService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

/**
 * Implémentation du service de génération PDF utilisant OpenPDF
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class PdfServiceImpl implements PdfService {

    private final ClientService clientService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 24, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL);
    private static final Font SMALL_FONT = new Font(Font.HELVETICA, 8, Font.NORMAL);

    @Override
    public byte[] generateQuotePdf(QuoteDto quoteDto) {
        // Validation des items
        if (quoteDto.getItems() == null || quoteDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot generate PDF: quote items cannot be null or empty");
        }

        try {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // Récupérer les informations du client
            ClientDto client = clientService.findById(quoteDto.getClientId());
            
            // En-tête
            addHeader(document, "DEVIS", quoteDto.getQuoteNumber());
            
            // Informations entreprise (à gauche) et client (à droite)
            addCompanyAndClientInfo(document, client, quoteDto.getDate(), quoteDto.getValidUntil());
            
            // Tableau des articles
            addItemsTable(document, quoteDto.getItems(), quoteDto.getTaxRate());
            
            // Totaux
            addTotals(document, quoteDto.getSubtotal(), quoteDto.getTaxRate(), 
                     quoteDto.getTaxAmount(), quoteDto.getTotal(), "TOTAL TTC");
            
            // Notes et conditions
            if (quoteDto.getNotes() != null && !quoteDto.getNotes().isEmpty()) {
                addNotes(document, quoteDto.getNotes());
            }
            
            if (quoteDto.getTermsAndConditions() != null && !quoteDto.getTermsAndConditions().isEmpty()) {
                addTermsAndConditions(document, quoteDto.getTermsAndConditions());
            }
            
            document.close();
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF du devis {}", quoteDto.getQuoteNumber(), e);
            throw new AppException("Erreur lors de la génération du PDF: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public byte[] generateInvoicePdf(InvoiceDto invoiceDto) {
        // Validation des items
        if (invoiceDto.getItems() == null || invoiceDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot generate PDF: invoice items cannot be null or empty");
        }

        try {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // Récupérer les informations du client
            ClientDto client = clientService.findById(invoiceDto.getClientId());
            
            // En-tête
            addHeader(document, "FACTURE", invoiceDto.getInvoiceNumber());
            
            // Informations entreprise (à gauche) et client (à droite)
            addCompanyAndClientInfoForInvoice(document, client, invoiceDto.getDate(), 
                                             invoiceDto.getDueDate(), invoiceDto.getPaidDate());
            
            // Référence devis si applicable
            if (invoiceDto.getQuoteId() != null && invoiceDto.getQuoteNumber() != null) {
                addQuoteReference(document, invoiceDto.getQuoteNumber());
            }
            
            // Tableau des articles
            addItemsTable(document, invoiceDto.getItems(), invoiceDto.getTaxRate());
            
            // Totaux
            addTotals(document, invoiceDto.getSubtotal(), invoiceDto.getTaxRate(), 
                     invoiceDto.getTaxAmount(), invoiceDto.getTotal(), "TOTAL TTC");
            
            // Statut de paiement
            addPaymentStatus(document, invoiceDto.getStatus().toString(), invoiceDto.getPaidDate());
            
            // Notes et conditions
            if (invoiceDto.getNotes() != null && !invoiceDto.getNotes().isEmpty()) {
                addNotes(document, invoiceDto.getNotes());
            }
            
            if (invoiceDto.getTermsAndConditions() != null && !invoiceDto.getTermsAndConditions().isEmpty()) {
                addTermsAndConditions(document, invoiceDto.getTermsAndConditions());
            }
            
            document.close();
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF de la facture {}", invoiceDto.getInvoiceNumber(), e);
            throw new AppException("Erreur lors de la génération du PDF: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void addHeader(Document document, String documentType, String documentNumber) throws DocumentException {
        Paragraph header = new Paragraph(documentType, TITLE_FONT);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(10);
        document.add(header);
        
        Paragraph number = new Paragraph("N° " + documentNumber, HEADER_FONT);
        number.setAlignment(Element.ALIGN_CENTER);
        number.setSpacingAfter(20);
        document.add(number);
    }

    private void addCompanyAndClientInfo(Document document, ClientDto client, 
                                        java.time.LocalDate date, java.time.LocalDate validUntil) 
            throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 1});
        table.setSpacingAfter(20);
        
        // Colonne gauche - Entreprise (à personnaliser selon vos besoins)
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(0);
        companyCell.addElement(new Paragraph("ERP-LITE", HEADER_FONT));
        companyCell.addElement(new Paragraph("Votre entreprise", NORMAL_FONT));
        companyCell.addElement(new Paragraph("Votre adresse", NORMAL_FONT));
        companyCell.addElement(new Paragraph("Votre ville, Code postal", NORMAL_FONT));
        companyCell.addElement(new Paragraph("Tél: Votre téléphone", NORMAL_FONT));
        companyCell.addElement(new Paragraph("Email: Votre email", NORMAL_FONT));
        table.addCell(companyCell);
        
        // Colonne droite - Client
        PdfPCell clientCell = new PdfPCell();
        clientCell.setBorder(0);
        clientCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        clientCell.addElement(new Paragraph("Client:", HEADER_FONT));
        clientCell.addElement(new Paragraph(getClientName(client), NORMAL_FONT));
        if (client.getAddress() != null && !client.getAddress().isEmpty()) {
            clientCell.addElement(new Paragraph(client.getAddress(), NORMAL_FONT));
        }
        if (client.getCity() != null && !client.getCity().isEmpty()) {
            String cityLine = client.getCity();
            if (client.getPostalCode() != null && !client.getPostalCode().isEmpty()) {
                cityLine = client.getPostalCode() + " " + cityLine;
            }
            clientCell.addElement(new Paragraph(cityLine, NORMAL_FONT));
        }
        if (client.getEmail() != null && !client.getEmail().isEmpty()) {
            clientCell.addElement(new Paragraph(client.getEmail(), NORMAL_FONT));
        }
        table.addCell(clientCell);
        
        document.add(table);
        
        // Dates
        Paragraph dateParagraph = new Paragraph();
        dateParagraph.add(new Chunk("Date: ", HEADER_FONT));
        dateParagraph.add(new Chunk(date.format(DATE_FORMATTER), NORMAL_FONT));
        if (validUntil != null) {
            dateParagraph.add(new Chunk("  |  Valable jusqu'au: ", HEADER_FONT));
            dateParagraph.add(new Chunk(validUntil.format(DATE_FORMATTER), NORMAL_FONT));
        }
        dateParagraph.setSpacingAfter(15);
        document.add(dateParagraph);
    }

    private void addCompanyAndClientInfoForInvoice(Document document, ClientDto client, 
                                                   java.time.LocalDate date, 
                                                   java.time.LocalDate dueDate,
                                                   java.time.LocalDate paidDate) 
            throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 1});
        table.setSpacingAfter(20);
        
        // Colonne gauche - Entreprise
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(0);
        companyCell.addElement(new Paragraph("ERP-LITE", HEADER_FONT));
        companyCell.addElement(new Paragraph("Votre entreprise", NORMAL_FONT));
        companyCell.addElement(new Paragraph("Votre adresse", NORMAL_FONT));
        companyCell.addElement(new Paragraph("Votre ville, Code postal", NORMAL_FONT));
        companyCell.addElement(new Paragraph("Tél: Votre téléphone", NORMAL_FONT));
        companyCell.addElement(new Paragraph("Email: Votre email", NORMAL_FONT));
        table.addCell(companyCell);
        
        // Colonne droite - Client
        PdfPCell clientCell = new PdfPCell();
        clientCell.setBorder(0);
        clientCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        clientCell.addElement(new Paragraph("Client:", HEADER_FONT));
        clientCell.addElement(new Paragraph(getClientName(client), NORMAL_FONT));
        if (client.getAddress() != null && !client.getAddress().isEmpty()) {
            clientCell.addElement(new Paragraph(client.getAddress(), NORMAL_FONT));
        }
        if (client.getCity() != null && !client.getCity().isEmpty()) {
            String cityLine = client.getCity();
            if (client.getPostalCode() != null && !client.getPostalCode().isEmpty()) {
                cityLine = client.getPostalCode() + " " + cityLine;
            }
            clientCell.addElement(new Paragraph(cityLine, NORMAL_FONT));
        }
        table.addCell(clientCell);
        
        document.add(table);
        
        // Dates
        Paragraph dateParagraph = new Paragraph();
        dateParagraph.add(new Chunk("Date: ", HEADER_FONT));
        dateParagraph.add(new Chunk(date.format(DATE_FORMATTER), NORMAL_FONT));
        if (dueDate != null) {
            dateParagraph.add(new Chunk("  |  Échéance: ", HEADER_FONT));
            dateParagraph.add(new Chunk(dueDate.format(DATE_FORMATTER), NORMAL_FONT));
        }
        dateParagraph.setSpacingAfter(15);
        document.add(dateParagraph);
    }

    private void addQuoteReference(Document document, String quoteNumber) throws DocumentException {
        Paragraph ref = new Paragraph();
        ref.add(new Chunk("Référence devis: ", HEADER_FONT));
        ref.add(new Chunk(quoteNumber, NORMAL_FONT));
        ref.setSpacingAfter(10);
        document.add(ref);
    }

    @SuppressWarnings("rawtypes")
    private void addItemsTable(Document document, java.util.List items, BigDecimal taxRate) 
            throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 1, 1.5f, 1.5f, 1.5f});
        
        // En-têtes
        addTableHeader(table, "Description");
        addTableHeader(table, "Qté");
        addTableHeader(table, "Prix unitaire HT");
        addTableHeader(table, "Taux TVA");
        addTableHeader(table, "Total HT");
        
        // Lignes d'articles
        if (items != null) {
            for (Object item : items) {
                if (item instanceof QuoteItemDto) {
                    QuoteItemDto quoteItem = (QuoteItemDto) item;
                    addItemRow(table, quoteItem.getDescription(), quoteItem.getQuantity(), 
                              quoteItem.getUnitPrice(), taxRate, quoteItem.getTotal());
                } else if (item instanceof InvoiceItemDto) {
                    InvoiceItemDto invoiceItem = (InvoiceItemDto) item;
                    addItemRow(table, invoiceItem.getDescription(), invoiceItem.getQuantity(), 
                              invoiceItem.getUnitPrice(), taxRate, invoiceItem.getTotal());
                }
            }
        }
        
        table.setSpacingAfter(20);
        document.add(table);
    }

    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, HEADER_FONT));
        cell.setBackgroundColor(new Color(220, 220, 220));
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addItemRow(PdfPTable table, String description, Integer quantity, 
                           BigDecimal unitPrice, BigDecimal taxRate, BigDecimal total) {
        table.addCell(new Paragraph(description, NORMAL_FONT));
        table.addCell(new Paragraph(String.valueOf(quantity), NORMAL_FONT));
        table.addCell(new Paragraph(formatCurrency(unitPrice), NORMAL_FONT));
        table.addCell(new Paragraph(formatPercentage(taxRate), NORMAL_FONT));
        table.addCell(new Paragraph(formatCurrency(total), NORMAL_FONT));
    }

    private void addTotals(Document document, BigDecimal subtotal, BigDecimal taxRate, 
                          BigDecimal taxAmount, BigDecimal total, String totalLabel) 
            throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setWidths(new float[]{2, 1});
        
        PdfPCell labelCell = new PdfPCell(new Paragraph("Sous-total HT:", NORMAL_FONT));
        labelCell.setBorder(0);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);
        table.addCell(new Paragraph(formatCurrency(subtotal), NORMAL_FONT));
        
        labelCell = new PdfPCell(new Paragraph("TVA (" + formatPercentage(taxRate) + "):", NORMAL_FONT));
        labelCell.setBorder(0);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);
        table.addCell(new Paragraph(formatCurrency(taxAmount), NORMAL_FONT));
        
        labelCell = new PdfPCell(new Paragraph(totalLabel + ":", HEADER_FONT));
        labelCell.setBorder(0);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPaddingTop(10);
        table.addCell(labelCell);
        PdfPCell totalCell = new PdfPCell(new Paragraph(formatCurrency(total), HEADER_FONT));
        totalCell.setPaddingTop(10);
        table.addCell(totalCell);
        
        table.setSpacingAfter(20);
        document.add(table);
    }

    private void addPaymentStatus(Document document, String status, java.time.LocalDate paidDate) 
            throws DocumentException {
        Paragraph statusParagraph = new Paragraph();
        statusParagraph.add(new Chunk("Statut: ", HEADER_FONT));
        statusParagraph.add(new Chunk(status, NORMAL_FONT));
        if (paidDate != null) {
            statusParagraph.add(new Chunk(" (Payé le: " + paidDate.format(DATE_FORMATTER) + ")", NORMAL_FONT));
        }
        statusParagraph.setSpacingAfter(15);
        document.add(statusParagraph);
    }

    private void addNotes(Document document, String notes) throws DocumentException {
        Paragraph notesParagraph = new Paragraph("Notes:", HEADER_FONT);
        notesParagraph.setSpacingBefore(10);
        document.add(notesParagraph);
        
        Paragraph notesContent = new Paragraph(notes, NORMAL_FONT);
        notesContent.setSpacingAfter(15);
        document.add(notesContent);
    }

    private void addTermsAndConditions(Document document, String terms) throws DocumentException {
        Paragraph termsParagraph = new Paragraph("Conditions générales:", HEADER_FONT);
        termsParagraph.setSpacingBefore(10);
        document.add(termsParagraph);
        
        Paragraph termsContent = new Paragraph(terms, SMALL_FONT);
        termsContent.setSpacingAfter(15);
        document.add(termsContent);
    }

    private String getClientName(ClientDto client) {
        if (client.getCompanyName() != null && !client.getCompanyName().isEmpty()) {
            return client.getCompanyName();
        }
        if (client.getContactFirstName() != null && client.getContactLastName() != null) {
            return client.getContactFirstName() + " " + client.getContactLastName();
        }
        if (client.getNom() != null && !client.getNom().isEmpty()) {
            return client.getNom();
        }
        return "Client";
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0,00 €";
        }
        return String.format("%.2f", amount.setScale(2, RoundingMode.HALF_UP)) + " €";
    }

    private String formatPercentage(BigDecimal percentage) {
        if (percentage == null) {
            return "0%";
        }
        return percentage.setScale(2, RoundingMode.HALF_UP) + "%";
    }
}

