package com.gp_dev.erp_lite.dtos;

import com.gp_dev.erp_lite.models.QuoteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO simplifié pour l'affichage des devis dans le portail client
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientPortalQuoteDto {

    private Long id;
    private String quoteNumber;
    private LocalDate date;
    private LocalDate validUntil;
    private QuoteStatus status;
    private String statusDisplayName;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private String notes;
    private List<QuoteItemDto> items;
    private String projectName;
    private Boolean canAccept;
    private Boolean canReject;
}

