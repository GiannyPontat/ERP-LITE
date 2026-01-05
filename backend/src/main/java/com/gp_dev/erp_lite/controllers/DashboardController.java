package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.DashboardStatsDto;
import com.gp_dev.erp_lite.dtos.MonthlyRevenueDto;
import com.gp_dev.erp_lite.dtos.TopClientDto;
import com.gp_dev.erp_lite.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Dashboard", description = "Dashboard statistics and analytics endpoints")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/dashboard", produces = "application/json; charset=UTF-8")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get dashboard statistics", description = "Retrieves key business metrics and statistics for the dashboard",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = DashboardStatsDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<DashboardStatsDto> getStats() {
        log.info("Get dashboard stats request received");
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @Operation(summary = "Get monthly revenue", description = "Retrieves monthly revenue data for a specific year or current year",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Revenue data retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/monthly-revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<MonthlyRevenueDto>> getMonthlyRevenue(
            @RequestParam(required = false) Integer year) {
        log.info("Get monthly revenue request received for year: {}", year);
        return ResponseEntity.ok(dashboardService.getMonthlyRevenue(year));
    }

    @Operation(summary = "Get top clients", description = "Retrieves the top 10 clients by revenue",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top clients retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/top-clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<TopClientDto>> getTopClients() {
        log.info("Get top clients request received");
        return ResponseEntity.ok(dashboardService.getTopClients());
    }

    @Operation(summary = "Get statistics by period", description = "Retrieves business metrics filtered by a specific date range",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = DashboardStatsDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/stats/period")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<DashboardStatsDto> getStatsByPeriod(
            @Parameter(description = "Start date (format: yyyy-MM-dd)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (format: yyyy-MM-dd)", example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Get dashboard stats by period request: {} to {}", startDate, endDate);
        return ResponseEntity.ok(dashboardService.getStatsByPeriod(startDate, endDate));
    }

    @Operation(summary = "Get top clients by period", description = "Retrieves top clients by revenue filtered by date range",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top clients retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/top-clients/period")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<TopClientDto>> getTopClientsByPeriod(
            @Parameter(description = "Start date (format: yyyy-MM-dd)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (format: yyyy-MM-dd)", example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Number of top clients to return (default: 10)", example = "10")
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        log.info("Get top clients by period request: {} to {}, limit: {}", startDate, endDate, limit);
        return ResponseEntity.ok(dashboardService.getTopClientsByPeriod(startDate, endDate, limit));
    }
}

