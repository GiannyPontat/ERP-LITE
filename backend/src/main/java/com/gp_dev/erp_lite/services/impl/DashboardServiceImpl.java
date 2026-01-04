package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.DashboardStatsDto;
import com.gp_dev.erp_lite.dtos.MonthlyRevenueDto;
import com.gp_dev.erp_lite.dtos.TopClientDto;
import com.gp_dev.erp_lite.models.Invoice;
import com.gp_dev.erp_lite.models.InvoiceStatus;
import com.gp_dev.erp_lite.models.Quote;
import com.gp_dev.erp_lite.models.QuoteStatus;
import com.gp_dev.erp_lite.repositories.ClientRepo;
import com.gp_dev.erp_lite.repositories.InvoiceRepo;
import com.gp_dev.erp_lite.repositories.QuoteRepo;
import com.gp_dev.erp_lite.services.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final InvoiceRepo invoiceRepo;
    private final QuoteRepo quoteRepo;
    private final ClientRepo clientRepo;

    @Override
    public DashboardStatsDto getStats() {
        log.info("Calculating dashboard statistics");

        // CA total = somme des factures payées
        BigDecimal totalRevenue = invoiceRepo.findAll().stream()
                .filter(invoice -> invoice.getStatus() == InvoiceStatus.PAID)
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // Bénéfices (simplifié: CA pour l'instant, car pas de coûts)
        BigDecimal totalProfit = totalRevenue;

        // Factures impayées (SENT, OVERDUE, PARTIALLY_PAID)
        List<Invoice> unpaidInvoices = invoiceRepo.findAll().stream()
                .filter(invoice -> invoice.getStatus() == InvoiceStatus.SENT 
                        || invoice.getStatus() == InvoiceStatus.OVERDUE 
                        || invoice.getStatus() == InvoiceStatus.PARTIALLY_PAID)
                .collect(Collectors.toList());

        Long unpaidInvoicesCount = (long) unpaidInvoices.size();
        BigDecimal unpaidInvoicesAmount = unpaidInvoices.stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // Devis en cours (non convertis, non rejetés, non expirés)
        Long activeQuotesCount = quoteRepo.findAll().stream()
                .filter(quote -> quote.getStatus() != QuoteStatus.CONVERTED 
                        && quote.getStatus() != QuoteStatus.REJECTED 
                        && quote.getStatus() != QuoteStatus.EXPIRED)
                .count();

        // Compteurs totaux
        Long totalClientsCount = clientRepo.count();
        Long totalQuotesCount = quoteRepo.count();
        Long totalInvoicesCount = invoiceRepo.count();

        return DashboardStatsDto.builder()
                .totalRevenue(totalRevenue)
                .totalProfit(totalProfit)
                .unpaidInvoicesCount(unpaidInvoicesCount)
                .unpaidInvoicesAmount(unpaidInvoicesAmount)
                .activeQuotesCount(activeQuotesCount)
                .totalClientsCount(totalClientsCount)
                .totalQuotesCount(totalQuotesCount)
                .totalInvoicesCount(totalInvoicesCount)
                .build();
    }

    @Override
    public List<MonthlyRevenueDto> getMonthlyRevenue(Integer year) {
        final int targetYear = (year == null) ? LocalDate.now().getYear() : year;

        log.info("Calculating monthly revenue for year: {}", targetYear);

        List<Invoice> paidInvoices = invoiceRepo.findAll().stream()
                .filter(invoice -> invoice.getStatus() == InvoiceStatus.PAID)
                .filter(invoice -> invoice.getDate().getYear() == targetYear)
                .collect(Collectors.toList());

        // Grouper par mois
        List<MonthlyRevenueDto> monthlyRevenues = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(targetYear, month);
            LocalDate startOfMonth = yearMonth.atDay(1);
            LocalDate endOfMonth = yearMonth.atEndOfMonth();

            BigDecimal monthlyRevenue = paidInvoices.stream()
                    .filter(invoice -> !invoice.getDate().isBefore(startOfMonth)
                            && !invoice.getDate().isAfter(endOfMonth))
                    .map(Invoice::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            monthlyRevenues.add(MonthlyRevenueDto.builder()
                    .year(targetYear)
                    .month(month)
                    .revenue(monthlyRevenue)
                    .build());
        }

        return monthlyRevenues;
    }

    @Override
    public List<TopClientDto> getTopClients() {
        log.info("Retrieving top 10 clients by revenue");
        return invoiceRepo.findTop10Clients();
    }
}

