package com.gp_dev.erp_lite.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkAsPaidRequest {

    private LocalDate paidDate; // Optionnel, par défaut = aujourd'hui

    private String paymentMethod; // Optionnel : CB, Virement, Chèque, Espèces

    private String notes; // Note optionnelle sur le paiement
}
