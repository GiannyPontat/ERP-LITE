package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.QuoteDto;
import com.gp_dev.erp_lite.dtos.QuoteItemDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.*;
import com.gp_dev.erp_lite.repositories.*;
import com.gp_dev.erp_lite.services.NumberGeneratorService;
import com.gp_dev.erp_lite.services.QuoteService;
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
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepo quoteRepo;
    private final QuoteItemRepo quoteItemRepo;
    private final ClientRepo clientRepo;
    private final UserRepo userRepo;
    private final NumberGeneratorService numberGeneratorService;

    @Override
    public List<QuoteDto> findAll() {
        return quoteRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public QuoteDto findById(Long id) {
        Quote quote = quoteRepo.findById(id)
                .orElseThrow(() -> new AppException("Quote not found", HttpStatus.NOT_FOUND));
        return toDto(quote);
    }

    @Override
    public QuoteDto create(QuoteDto quoteDto) {
        // Vérifier que le client existe
        Client client = clientRepo.findById(quoteDto.getClientId())
                .orElseThrow(() -> new AppException("Client not found", HttpStatus.NOT_FOUND));

        // Vérifier que l'utilisateur existe
        User user = userRepo.findById(quoteDto.getCreatedById())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        // Générer le numéro de devis
        String quoteNumber = numberGeneratorService.generateQuoteNumber();

        // Créer le devis
        Quote quote = Quote.builder()
                .quoteNumber(quoteNumber)
                .client(client)
                .createdBy(user)
                .date(quoteDto.getDate())
                .validUntil(quoteDto.getValidUntil())
                .status(quoteDto.getStatus() != null ? quoteDto.getStatus() : QuoteStatus.DRAFT)
                .notes(quoteDto.getNotes())
                .termsAndConditions(quoteDto.getTermsAndConditions())
                .build();

        // Sauvegarder le devis
        quote = quoteRepo.save(quote);

        // Ajouter les items et calculer les totaux
        if (quoteDto.getItems() != null && !quoteDto.getItems().isEmpty()) {
            BigDecimal subtotal = BigDecimal.ZERO;
            
            for (QuoteItemDto itemDto : quoteDto.getItems()) {
                QuoteItem item = QuoteItem.builder()
                        .quote(quote)
                        .description(itemDto.getDescription())
                        .quantity(itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .build();
                
                // Calculer le total de l'item
                BigDecimal itemTotal = itemDto.getUnitPrice()
                        .multiply(BigDecimal.valueOf(itemDto.getQuantity()))
                        .setScale(2, RoundingMode.HALF_UP);
                item.setTotal(itemTotal);
                
                subtotal = subtotal.add(itemTotal);
                quoteItemRepo.save(item);
            }

            // Calculer les taxes et le total
            BigDecimal taxRate = quoteDto.getTaxRate() != null ? quoteDto.getTaxRate() : BigDecimal.ZERO;
            BigDecimal taxAmount = subtotal.multiply(taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(taxAmount);

            quote.setSubtotal(subtotal);
            quote.setTaxRate(taxRate);
            quote.setTaxAmount(taxAmount);
            quote.setTotal(total);
        } else {
            quote.setSubtotal(quoteDto.getSubtotal() != null ? quoteDto.getSubtotal() : BigDecimal.ZERO);
            quote.setTaxRate(quoteDto.getTaxRate() != null ? quoteDto.getTaxRate() : BigDecimal.ZERO);
            quote.setTaxAmount(quoteDto.getTaxAmount() != null ? quoteDto.getTaxAmount() : BigDecimal.ZERO);
            quote.setTotal(quoteDto.getTotal() != null ? quoteDto.getTotal() : BigDecimal.ZERO);
        }

        quote = quoteRepo.save(quote);
        return toDto(quote);
    }

    @Override
    public QuoteDto update(Long id, QuoteDto quoteDto) {
        Quote quote = quoteRepo.findById(id)
                .orElseThrow(() -> new AppException("Quote not found", HttpStatus.NOT_FOUND));

        // Mettre à jour les champs
        if (quoteDto.getClientId() != null) {
            Client client = clientRepo.findById(quoteDto.getClientId())
                    .orElseThrow(() -> new AppException("Client not found", HttpStatus.NOT_FOUND));
            quote.setClient(client);
        }

        if (quoteDto.getDate() != null) {
            quote.setDate(quoteDto.getDate());
        }

        if (quoteDto.getValidUntil() != null) {
            quote.setValidUntil(quoteDto.getValidUntil());
        }

        if (quoteDto.getStatus() != null) {
            quote.setStatus(quoteDto.getStatus());
        }

        quote.setNotes(quoteDto.getNotes());
        quote.setTermsAndConditions(quoteDto.getTermsAndConditions());

        // Supprimer les anciens items
        quoteItemRepo.deleteByQuoteId(id);

        // Ajouter les nouveaux items et recalculer
        if (quoteDto.getItems() != null && !quoteDto.getItems().isEmpty()) {
            BigDecimal subtotal = BigDecimal.ZERO;

            for (QuoteItemDto itemDto : quoteDto.getItems()) {
                QuoteItem item = QuoteItem.builder()
                        .quote(quote)
                        .description(itemDto.getDescription())
                        .quantity(itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .build();

                BigDecimal itemTotal = itemDto.getUnitPrice()
                        .multiply(BigDecimal.valueOf(itemDto.getQuantity()))
                        .setScale(2, RoundingMode.HALF_UP);
                item.setTotal(itemTotal);

                subtotal = subtotal.add(itemTotal);
                quoteItemRepo.save(item);
            }

            BigDecimal taxRate = quoteDto.getTaxRate() != null ? quoteDto.getTaxRate() : BigDecimal.ZERO;
            BigDecimal taxAmount = subtotal.multiply(taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(taxAmount);

            quote.setSubtotal(subtotal);
            quote.setTaxRate(taxRate);
            quote.setTaxAmount(taxAmount);
            quote.setTotal(total);
        } else {
            if (quoteDto.getSubtotal() != null) quote.setSubtotal(quoteDto.getSubtotal());
            if (quoteDto.getTaxRate() != null) quote.setTaxRate(quoteDto.getTaxRate());
            if (quoteDto.getTaxAmount() != null) quote.setTaxAmount(quoteDto.getTaxAmount());
            if (quoteDto.getTotal() != null) quote.setTotal(quoteDto.getTotal());
        }

        quote = quoteRepo.save(quote);
        return toDto(quote);
    }

    @Override
    public void delete(Long id) {
        if (!quoteRepo.existsById(id)) {
            throw new AppException("Quote not found", HttpStatus.NOT_FOUND);
        }
        quoteRepo.deleteById(id);
    }

    @Override
    public List<QuoteDto> findByClientId(Long clientId) {
        return quoteRepo.findByClientId(clientId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuoteDto> findByStatus(String status) {
        try {
            QuoteStatus quoteStatus = QuoteStatus.valueOf(status.toUpperCase());
            return quoteRepo.findByStatus(quoteStatus).stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new AppException("Invalid status: " + status, HttpStatus.BAD_REQUEST);
        }
    }

    private QuoteDto toDto(Quote quote) {
        List<QuoteItemDto> items = quote.getItems() != null
                ? quote.getItems().stream()
                        .map(item -> QuoteItemDto.builder()
                                .id(item.getId())
                                .description(item.getDescription())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .total(item.getTotal())
                                .build())
                        .collect(Collectors.toList())
                : List.of();

        String clientName = quote.getClient() != null
                ? (quote.getClient().getCompanyName() != null
                        ? quote.getClient().getCompanyName()
                        : quote.getClient().getNom())
                : null;

        String createdByEmail = quote.getCreatedBy() != null
                ? quote.getCreatedBy().getEmail()
                : null;

        return QuoteDto.builder()
                .id(quote.getId())
                .quoteNumber(quote.getQuoteNumber())
                .clientId(quote.getClient() != null ? quote.getClient().getId() : null)
                .clientName(clientName)
                .createdById(quote.getCreatedBy() != null ? quote.getCreatedBy().getId() : null)
                .createdByEmail(createdByEmail)
                .date(quote.getDate())
                .validUntil(quote.getValidUntil())
                .status(quote.getStatus())
                .subtotal(quote.getSubtotal())
                .taxRate(quote.getTaxRate())
                .taxAmount(quote.getTaxAmount())
                .total(quote.getTotal())
                .notes(quote.getNotes())
                .termsAndConditions(quote.getTermsAndConditions())
                .items(items)
                .build();
    }
}

