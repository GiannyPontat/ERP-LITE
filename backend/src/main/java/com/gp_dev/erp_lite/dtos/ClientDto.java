package com.gp_dev.erp_lite.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientDto {
    private Long id;

    // Nouveaux champs selon spec
    private String companyName;
    private String siret;
    private String contactFirstName;
    private String contactLastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String postalCode;
    private Integer paymentTerms;
    private String notes;
    private Long userId; // ID de l'utilisateur propriétaire

    // Champs de compatibilité pour l'ancien système
    private String nom;
    private String entreprise;
    private String telephone;
    private String adresse;
}
