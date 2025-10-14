package com.gp_dev.erp_lite.dtos;

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
    private String statut;
    private LocalDate dateCreation;
    private double totalHT;
    private double totalTTC;
    private List<LigneDevisDto> lignes;

}
