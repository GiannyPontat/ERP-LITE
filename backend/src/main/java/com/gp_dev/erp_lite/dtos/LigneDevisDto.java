package com.gp_dev.erp_lite.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LigneDevisDto {
    private Long id;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotNull(message = "Quantite is required")
    @Min(value = 1, message = "Quantite must be at least 1")
    private Integer quantite;

    @NotNull(message = "Prix unitaire is required")
    @Min(value = 0, message = "Prix unitaire must be greater than or equal to 0")
    private Double prixUnitaire;
}