package com.gp_dev.erp_lite.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private BigDecimal totalRevenue; // CA total
    private BigDecimal totalProfit; // Bénéfices (CA - coûts, simplifié ici = CA)
    private Long unpaidInvoicesCount; // Nombre de factures impayées
    private BigDecimal unpaidInvoicesAmount; // Montant des factures impayées
    private Long activeQuotesCount; // Nombre de devis en cours (non convertis, non rejetés)
    private Long totalClientsCount; // Nombre total de clients
    private Long totalQuotesCount; // Nombre total de devis
    private Long totalInvoicesCount; // Nombre total de factures
}

