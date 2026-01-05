package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.DashboardStatsDto;
import com.gp_dev.erp_lite.dtos.MonthlyRevenueDto;
import com.gp_dev.erp_lite.dtos.TopClientDto;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    DashboardStatsDto getStats();
    DashboardStatsDto getStatsByPeriod(LocalDate startDate, LocalDate endDate);
    List<MonthlyRevenueDto> getMonthlyRevenue(Integer year);
    List<TopClientDto> getTopClients();
    List<TopClientDto> getTopClientsByPeriod(LocalDate startDate, LocalDate endDate, Integer limit);
}

