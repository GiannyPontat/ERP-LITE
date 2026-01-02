package com.gp_dev.erp_lite.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DevisDto {
    private Long id;

    @NotBlank(message = "Statut is required")
    @Size(max = 50, message = "Statut must not exceed 50 characters")
    private String statut;

    @NotNull(message = "Date creation is required")
    @PastOrPresent(message = "Date creation must be in the past or present")
    private LocalDate dateCreation;

    @Min(value = 0, message = "Total HT must be greater than or equal to 0")
    private double totalHT;

    @Min(value = 0, message = "Total TTC must be greater than or equal to 0")
    private double totalTTC;

    @Valid
    private List<LigneDevisDto> lignes;
}
