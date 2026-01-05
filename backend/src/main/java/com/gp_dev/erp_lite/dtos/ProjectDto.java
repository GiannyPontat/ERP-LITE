package com.gp_dev.erp_lite.dtos;

import com.gp_dev.erp_lite.models.ProjectStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDto {

    private Long id;

    private String reference;

    @NotBlank(message = "Project name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @NotNull(message = "Client ID is required")
    private Long clientId;
    private String clientName;

    private Long managerId;
    private String managerName;

    @NotNull(message = "Created by ID is required")
    private Long createdById;
    private String createdByName;

    private ProjectStatus status;
    private String statusDisplayName;

    // Adresse du chantier
    @Size(max = 500, message = "Site address must not exceed 500 characters")
    private String siteAddress;

    @Size(max = 100, message = "Site city must not exceed 100 characters")
    private String siteCity;

    @Size(max = 20, message = "Site postal code must not exceed 20 characters")
    private String sitePostalCode;

    // Dates
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;

    // Budget
    @DecimalMin(value = "0.00", message = "Estimated budget must be greater than or equal to 0")
    private BigDecimal estimatedBudget;

    private BigDecimal actualCost;

    // Progression
    @Min(value = 0, message = "Progress must be between 0 and 100")
    @Max(value = 100, message = "Progress must be between 0 and 100")
    private Integer progressPercentage;

    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;

    // Compteurs
    private Integer quotesCount;
    private Integer invoicesCount;
    private Integer documentsCount;

    // Totaux
    private BigDecimal totalQuotesAmount;
    private BigDecimal totalInvoicesAmount;
    private BigDecimal totalPaidAmount;
    private BigDecimal remainingToPay;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Liste des documents
    private List<ProjectDocumentDto> documents;
}

