package com.gp_dev.erp_lite.dtos;

import com.gp_dev.erp_lite.models.CatalogCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CatalogItemDto {
    
    private Long id;

    @NotBlank(message = "Reference is required")
    @Size(max = 50, message = "Reference must not exceed 50 characters")
    private String reference;

    @NotBlank(message = "Designation is required")
    @Size(max = 500, message = "Designation must not exceed 500 characters")
    private String designation;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @NotNull(message = "Category is required")
    private CatalogCategory category;

    private String categoryDisplayName;

    @NotBlank(message = "Unit is required")
    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", message = "Unit price must be greater than or equal to 0")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.00", message = "Tax rate must be greater than or equal to 0")
    @DecimalMax(value = "100.00", message = "Tax rate must be less than or equal to 100")
    private BigDecimal taxRate;

    @DecimalMin(value = "0.00", message = "Cost price must be greater than or equal to 0")
    private BigDecimal costPrice;

    @Size(max = 255, message = "Supplier must not exceed 255 characters")
    private String supplier;

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    @Size(max = 100, message = "Manufacturer reference must not exceed 100 characters")
    private String manufacturerReference;

    private Boolean active;

    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;

    private Long userId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Champs calculés
    private BigDecimal margin; // unitPrice - costPrice
    private BigDecimal marginPercentage; // ((unitPrice - costPrice) / costPrice) * 100
}

