package com.gp_dev.erp_lite.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ClientDto {
    private Long id;
    private String nom;
    private String entreprise;
    private String email;
    private String telephone;
    private String adresse;
}
