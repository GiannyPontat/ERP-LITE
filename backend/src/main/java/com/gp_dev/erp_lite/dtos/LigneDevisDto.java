package com.gp_dev.erp_lite.dtos;

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
    private String description;
    private int quantite;
    private double prixUnitaire;
}