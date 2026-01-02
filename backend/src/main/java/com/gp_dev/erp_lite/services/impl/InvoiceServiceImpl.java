package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.InvoiceDto;
import com.gp_dev.erp_lite.dtos.InvoiceItemDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.*;
import com.gp_dev.erp_lite.repositories.*;
import com.gp_dev.erp_lite.services.InvoiceService;
import com.gp_dev.erp_lite.services.NumberGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepo invoiceRepo;
    private final InvoiceItemRepo invoiceItemRepo;
    private final QuoteRepo quoteRepo;
    private final ClientRepo clientRepo;
    private final UserRepo userRepo;
    private final NumberGeneratorService numberGeneratorService;

    @Override
    public List<InvoiceDto> findAll() {
        return invoiceRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto findById(Long id) {
        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> new AppException("Invoice not found", HttpStatus.NOT_FOUND));
        return toDto(invoice);
    }

    @Override
    public InvoiceDto create(InvoiceDto invoiceDto) {
        Client client = clientRepo.findById(invoiceDto.getClientId())
                .orElseThrow(() -> new AppException("Client not found", HttpStatus.NOT_FOUND));

        User user = userRepo.findById(invoiceDto.getCreatedById())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        String invoiceNumber = numberGeneratorService.generateInvoiceNumber();

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .client(client)
                .createdBy(user)
                .date(invoiceDto.getDate())
                .dueDate(invoiceDto.getDueDate())
                .status(invoiceDto.getStatus() != null ? invoiceDto.getStatus() : InvoiceStatus.DRAFT)
                .notes(invoiceDto.getNotes())
                .termsAndConditions(invoiceDto.getTermsAndConditions())
                .build();

        invoice = invoiceRepo.save(invoice);
        invoice = calculateAndSetTotals(invoice, invoiceDto);
        
        return toDto(invoiceRepo.save(invoice));
    }

    @Override
    public InvoiceDto createFromQuote(Long quoteId, InvoiceDto invoiceDto) {
        Quote quote = quoteRepo.findById(quoteId)
                .orElseThrow(() -> new AppException("Quote not found", HttpStatus.NOT_FOUND));

        Client client = quote.getClient();
        User user = invoiceDto.getCreatedById() != null
                ? userRepo.findById(invoiceDto.getCreatedById())
                        .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND))
                : quote.getCreatedBy();

        String invoiceNumber = numberGeneratorService.generateInvoiceNumber();

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .client(client)
                .createdBy(user)
                .quote(quote)
                .date(invoiceDto.getDate() != null ? invoiceDto.getDate() : java.time.LocalDate.now())
                .dueDate(invoiceDto.getDueDate())
                .status(InvoiceStatus.SENT)
                .subtotal(quote.getSubtotal())
                .taxRate(quote.getTaxRate())
                .taxAmount(quote.getTaxAmount())
                .total(quote.getTotal())
                .notes(invoiceDto.getNotes() != null ? invoiceDto.getNotes() : quote.getNotes())
                .termsAndConditions(invoiceDto.getTermsAndConditions() != null 
                        ? invoiceDto.getTermsAndConditions() 
                        : quote.getTermsAndConditions())
                .build();

        invoice = invoiceRepo.save(invoice);

        // Copier les items du devis vers la facture
        if (quote.getItems() != null && !quote.getItems().isEmpty()) {
            for (com.gp_dev.erp_lite.models.QuoteItem quoteItem : quote.getItems()) {
                InvoiceItem invoiceItem = InvoiceItem.builder()
                        .invoice(invoice)
                        .description(quoteItem.getDescription())
                        .quantity(quoteItem.getQuantity())
                        .unitPrice(quoteItem.getUnitPrice())
                        .total(quoteItem.getTotal())
                        .build();
                invoiceItemRepo.save(invoiceItem);
            }
        }

        // Mettre à jour le statut du devis
        quote.setStatus(QuoteStatus.CONVERTED);
        quoteRepo.save(quote);

        return toDto(invoice);
    }

    @Override
    public InvoiceDto update(Long id, InvoiceDto invoiceDto) {
        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> new AppException("Invoice not found", HttpStatus.NOT_FOUND));

        if (invoiceDto.getClientId() != null) {
            Client client = clientRepo.findById(invoiceDto.getClientId())
                    .orElseThrow(() -> new AppException("Client not found", HttpStatus.NOT_FOUND));
            invoice.setClient(client);
        }

        if (invoiceDto.getDate() != null) {
            invoice.setDate(invoiceDto.getDate());
        }

        if (invoiceDto.getDueDate() != null) {
            invoice.setDueDate(invoiceDto.getDueDate());
        }

        if (invoiceDto.getPaidDate() != null) {
            invoice.setPaidDate(invoiceDto.getPaidDate());
        }

        if (invoiceDto.getStatus() != null) {
            invoice.setStatus(invoiceDto.getStatus());
        }

        invoice.setNotes(invoiceDto.getNotes());
        invoice.setTermsAndConditions(invoiceDto.getTermsAndConditions());

        invoiceItemRepo.deleteByInvoiceId(id);
        invoice = calculateAndSetTotals(invoice, invoiceDto);

        return toDto(invoiceRepo.save(invoice));
    }

    @Override
    public void delete(Long id) {
        if (!invoiceRepo.existsById(id)) {
            throw new AppException("Invoice not found", HttpStatus.NOT_FOUND);
        }
        invoiceRepo.deleteById(id);
    }

    @Override
    public List<InvoiceDto> findByClientId(Long clientId) {
        return invoiceRepo.findByClientId(clientId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> findByStatus(String status) {
        try {
            InvoiceStatus invoiceStatus = InvoiceStatus.valueOf(status.toUpperCase());
            return invoiceRepo.findByStatus(invoiceStatus).stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new AppException("Invalid status: " + status, HttpStatus.BAD_REQUEST);
        }
    }

    private Invoice calculateAndSetTotals(Invoice invoice, InvoiceDto invoiceDto) {
        if (invoiceDto.getItems() != null && !invoiceDto.getItems().isEmpty()) {
            BigDecimal subtotal = BigDecimal.ZERO;

            for (InvoiceItemDto itemDto : invoiceDto.getItems()) {
                InvoiceItem item = InvoiceItem.builder()
                        .invoice(invoice)
                        .description(itemDto.getDescription())
                        .quantity(itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .build();

                BigDecimal itemTotal = itemDto.getUnitPrice()
                        .multiply(BigDecimal.valueOf(itemDto.getQuantity()))
                        .setScale(2, RoundingMode.HALF_UP);
                item.setTotal(itemTotal);

                subtotal = subtotal.add(itemTotal);
                invoiceItemRepo.save(item);
            }

            BigDecimal taxRate = invoiceDto.getTaxRate() != null ? invoiceDto.getTaxRate() : invoice.getTaxRate();
            BigDecimal taxAmount = subtotal.multiply(taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(taxAmount);

            invoice.setSubtotal(subtotal);
            invoice.setTaxRate(taxRate);
            invoice.setTaxAmount(taxAmount);
            invoice.setTotal(total);
        } else {
            if (invoiceDto.getSubtotal() != null) invoice.setSubtotal(invoiceDto.getSubtotal());
            if (invoiceDto.getTaxRate() != null) invoice.setTaxRate(invoiceDto.getTaxRate());
            if (invoiceDto.getTaxAmount() != null) invoice.setTaxAmount(invoiceDto.getTaxAmount());
            if (invoiceDto.getTotal() != null) invoice.setTotal(invoiceDto.getTotal());
        }
        return invoice;
    }

    private InvoiceDto toDto(Invoice invoice) {
        List<InvoiceItemDto> items = invoice.getItems() != null
                ? invoice.getItems().stream()
                        .map(item -> InvoiceItemDto.builder()
                                .id(item.getId())
                                .description(item.getDescription())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .total(item.getTotal())
                                .build())
                        .collect(Collectors.toList())
                : List.of();

        String clientName = invoice.getClient() != null
                ? (invoice.getClient().getCompanyName() != null
                        ? invoice.getClient().getCompanyName()
                        : invoice.getClient().getNom())
                : null;

        String createdByEmail = invoice.getCreatedBy() != null
                ? invoice.getCreatedBy().getEmail()
                : null;

        String quoteNumber = invoice.getQuote() != null
                ? invoice.getQuote().getQuoteNumber()
                : null;

        return InvoiceDto.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .clientId(invoice.getClient() != null ? invoice.getClient().getId() : null)
                .clientName(clientName)
                .createdById(invoice.getCreatedBy() != null ? invoice.getCreatedBy().getId() : null)
                .createdByEmail(createdByEmail)
                .quoteId(invoice.getQuote() != null ? invoice.getQuote().getId() : null)
                .quoteNumber(quoteNumber)
                .date(invoice.getDate())
                .dueDate(invoice.getDueDate())
                .paidDate(invoice.getPaidDate())
                .status(invoice.getStatus())
                .subtotal(invoice.getSubtotal())
                .taxRate(invoice.getTaxRate())
                .taxAmount(invoice.getTaxAmount())
                .total(invoice.getTotal())
                .notes(invoice.getNotes())
                .termsAndConditions(invoice.getTermsAndConditions())
                .items(items)
                .build();
    }
}

