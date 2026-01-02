package com.gp_dev.erp_lite.dtos;

import com.gp_dev.erp_lite.models.QuoteStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuoteDto {
    private Long id;
    
    private String quoteNumber;
    
    @NotNull(message = "Client ID is required")
    private Long clientId;
    
    private String clientName; // Pour l'affichage
    
    @NotNull(message = "Created by user ID is required")
    private Long createdById;
    
    private String createdByEmail; // Pour l'affichage
    
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date must be in the past or present")
    private LocalDate date;
    
    private LocalDate validUntil;
    
    @NotNull(message = "Status is required")
    private QuoteStatus status;
    
    @NotNull(message = "Subtotal is required")
    @Min(value = 0, message = "Subtotal must be greater than or equal to 0")
    private BigDecimal subtotal;
    
    @NotNull(message = "Tax rate is required")
    @Min(value = 0, message = "Tax rate must be greater than or equal to 0")
    private BigDecimal taxRate;
    
    @NotNull(message = "Tax amount is required")
    @Min(value = 0, message = "Tax amount must be greater than or equal to 0")
    private BigDecimal taxAmount;
    
    @NotNull(message = "Total is required")
    @Min(value = 0, message = "Total must be greater than or equal to 0")
    private BigDecimal total;
    
    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
    
    @Size(max = 10000, message = "Terms and conditions must not exceed 10000 characters")
    private String termsAndConditions;
    
    @Valid
    private List<QuoteItemDto> items;
}

