package com.gp_dev.erp_lite.dtos;

import com.gp_dev.erp_lite.models.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO simplifié pour l'affichage des factures dans le portail client
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientPortalInvoiceDto {

    private Long id;
    private String invoiceNumber;
    private LocalDate date;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private InvoiceStatus status;
    private String statusDisplayName;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private String notes;
    private List<InvoiceItemDto> items;
    private String projectName;
    private String quoteNumber;
    private Boolean isOverdue;
    private Long daysUntilDue;
}

