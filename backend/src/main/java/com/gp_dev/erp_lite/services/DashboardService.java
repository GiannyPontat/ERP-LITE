package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.DashboardStatsDto;
import com.gp_dev.erp_lite.dtos.MonthlyRevenueDto;
import com.gp_dev.erp_lite.dtos.TopClientDto;

import java.util.List;

public interface DashboardService {
    DashboardStatsDto getStats();
    List<MonthlyRevenueDto> getMonthlyRevenue(Integer year);
    List<TopClientDto> getTopClients();
}

