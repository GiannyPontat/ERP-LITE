package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.DashboardStatsDto;
import com.gp_dev.erp_lite.dtos.MonthlyRevenueDto;
import com.gp_dev.erp_lite.models.*;
import com.gp_dev.erp_lite.repositories.ClientRepo;
import com.gp_dev.erp_lite.repositories.InvoiceRepo;
import com.gp_dev.erp_lite.repositories.QuoteRepo;
import com.gp_dev.erp_lite.services.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private InvoiceRepo invoiceRepo;

    @Mock
    private QuoteRepo quoteRepo;

    @Mock
    private ClientRepo clientRepo;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private Client client;
    private User user;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id(1L)
                .companyName("Test Company")
                .build();

        user = User.builder()
                .id(1L)
                .email("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    @Test
    void testGetStats_WithData() {
        // Given
        Invoice paidInvoice = Invoice.builder()
                .id(1L)
                .invoiceNumber("FACT-2026-0001")
                .client(client)
                .createdBy(user)
                .date(LocalDate.now())
                .status(InvoiceStatus.PAID)
                .total(new BigDecimal("1500.00"))
                .build();

        Invoice sentInvoice = Invoice.builder()
                .id(2L)
                .invoiceNumber("FACT-2026-0002")
                .client(client)
                .createdBy(user)
                .date(LocalDate.now())
                .status(InvoiceStatus.SENT)
                .total(new BigDecimal("800.00"))
                .build();

        Quote draftQuote = Quote.builder()
                .id(1L)
                .quoteNumber("DEV-2026-0001")
                .client(client)
                .createdBy(user)
                .date(LocalDate.now())
                .status(QuoteStatus.DRAFT)
                .build();

        when(invoiceRepo.findAll()).thenReturn(Arrays.asList(paidInvoice, sentInvoice));
        when(quoteRepo.findAll()).thenReturn(Collections.singletonList(draftQuote));
        when(clientRepo.count()).thenReturn(10L);
        when(quoteRepo.count()).thenReturn(5L);
        when(invoiceRepo.count()).thenReturn(15L);

        // When
        DashboardStatsDto result = dashboardService.getStats();

        // Then
        assertNotNull(result);
        assertEquals(0, new BigDecimal("1500.00").compareTo(result.getTotalRevenue()));
        assertEquals(1L, result.getUnpaidInvoicesCount());
        assertEquals(0, new BigDecimal("800.00").compareTo(result.getUnpaidInvoicesAmount()));
        assertEquals(1L, result.getActiveQuotesCount());
        assertEquals(10L, result.getTotalClientsCount());
        assertEquals(5L, result.getTotalQuotesCount());
        assertEquals(15L, result.getTotalInvoicesCount());
    }

    @Test
    void testGetStats_Empty() {
        // Given
        when(invoiceRepo.findAll()).thenReturn(Collections.emptyList());
        when(quoteRepo.findAll()).thenReturn(Collections.emptyList());
        when(clientRepo.count()).thenReturn(0L);
        when(quoteRepo.count()).thenReturn(0L);
        when(invoiceRepo.count()).thenReturn(0L);

        // When
        DashboardStatsDto result = dashboardService.getStats();

        // Then
        assertNotNull(result);
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getTotalRevenue()));
        assertEquals(0L, result.getUnpaidInvoicesCount());
        assertEquals(0L, result.getActiveQuotesCount());
    }

    @Test
    void testGetMonthlyRevenue_CurrentYear() {
        // Given
        int currentYear = LocalDate.now().getYear();
        
        Invoice januaryInvoice = Invoice.builder()
                .id(1L)
                .invoiceNumber("FACT-2026-0001")
                .client(client)
                .createdBy(user)
                .date(LocalDate.of(currentYear, 1, 15))
                .status(InvoiceStatus.PAID)
                .total(new BigDecimal("1000.00"))
                .build();

        Invoice februaryInvoice = Invoice.builder()
                .id(2L)
                .invoiceNumber("FACT-2026-0002")
                .client(client)
                .createdBy(user)
                .date(LocalDate.of(currentYear, 2, 20))
                .status(InvoiceStatus.PAID)
                .total(new BigDecimal("2000.00"))
                .build();

        when(invoiceRepo.findAll()).thenReturn(Arrays.asList(januaryInvoice, februaryInvoice));

        // When
        List<MonthlyRevenueDto> result = dashboardService.getMonthlyRevenue(null);

        // Then
        assertNotNull(result);
        assertEquals(12, result.size()); // 12 months

        // Check January
        MonthlyRevenueDto january = result.stream()
                .filter(m -> m.getMonth() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(january);
        assertEquals(0, new BigDecimal("1000.00").compareTo(january.getRevenue()));

        // Check February
        MonthlyRevenueDto february = result.stream()
                .filter(m -> m.getMonth() == 2)
                .findFirst()
                .orElse(null);
        assertNotNull(february);
        assertEquals(0, new BigDecimal("2000.00").compareTo(february.getRevenue()));
    }

    @Test
    void testGetMonthlyRevenue_SpecificYear() {
        // Given
        when(invoiceRepo.findAll()).thenReturn(Collections.emptyList());

        // When
        List<MonthlyRevenueDto> result = dashboardService.getMonthlyRevenue(2025);

        // Then
        assertNotNull(result);
        assertEquals(12, result.size());
        assertTrue(result.stream().allMatch(m -> m.getYear() == 2025));
        assertTrue(result.stream().allMatch(m -> m.getRevenue().compareTo(BigDecimal.ZERO) == 0));
    }

    @Test
    void testGetStatsByPeriod_WithData() {
        // Given
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);

        Invoice paidInvoiceInPeriod = Invoice.builder()
                .id(1L)
                .invoiceNumber("FACT-2026-0001")
                .client(client)
                .createdBy(user)
                .date(LocalDate.of(2026, 3, 15))
                .status(InvoiceStatus.PAID)
                .total(new BigDecimal("1500.00"))
                .build();

        Invoice paidInvoiceOutOfPeriod = Invoice.builder()
                .id(2L)
                .invoiceNumber("FACT-2026-0002")
                .client(client)
                .createdBy(user)
                .date(LocalDate.of(2026, 8, 15)) // Outside period
                .status(InvoiceStatus.PAID)
                .total(new BigDecimal("500.00"))
                .build();

        Invoice unpaidInvoice = Invoice.builder()
                .id(3L)
                .invoiceNumber("FACT-2026-0003")
                .client(client)
                .createdBy(user)
                .date(LocalDate.of(2026, 2, 10))
                .status(InvoiceStatus.SENT)
                .total(new BigDecimal("800.00"))
                .build();

        Quote activeQuote = Quote.builder()
                .id(1L)
                .quoteNumber("DEV-2026-0001")
                .client(client)
                .createdBy(user)
                .date(LocalDate.of(2026, 4, 1))
                .status(QuoteStatus.DRAFT)
                .build();

        when(invoiceRepo.findAll()).thenReturn(Arrays.asList(paidInvoiceInPeriod, paidInvoiceOutOfPeriod, unpaidInvoice));
        when(quoteRepo.findAll()).thenReturn(Collections.singletonList(activeQuote));

        // When
        DashboardStatsDto result = dashboardService.getStatsByPeriod(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(0, new BigDecimal("1500.00").compareTo(result.getTotalRevenue())); // Only invoice in period
        assertEquals(1L, result.getUnpaidInvoicesCount());
        assertEquals(0, new BigDecimal("800.00").compareTo(result.getUnpaidInvoicesAmount()));
        assertEquals(1L, result.getActiveQuotesCount());
        assertEquals(2L, result.getTotalInvoicesCount()); // 2 invoices in period
    }

    @Test
    void testGetStatsByPeriod_EmptyPeriod() {
        // Given
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 12, 31);

        Invoice invoice2026 = Invoice.builder()
                .id(1L)
                .invoiceNumber("FACT-2026-0001")
                .client(client)
                .createdBy(user)
                .date(LocalDate.of(2026, 1, 1))
                .status(InvoiceStatus.PAID)
                .total(new BigDecimal("1000.00"))
                .build();

        when(invoiceRepo.findAll()).thenReturn(Collections.singletonList(invoice2026));
        when(quoteRepo.findAll()).thenReturn(Collections.emptyList());

        // When
        DashboardStatsDto result = dashboardService.getStatsByPeriod(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getTotalRevenue()));
        assertEquals(0L, result.getTotalInvoicesCount());
    }

    @Test
    void testGetTopClientsByPeriod() {
        // Given
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);

        Client client1 = Client.builder()
                .id(1L)
                .companyName("Client 1")
                .build();

        Client client2 = Client.builder()
                .id(2L)
                .companyName("Client 2")
                .build();

        Invoice invoice1Client1 = Invoice.builder()
                .id(1L)
                .client(client1)
                .date(LocalDate.of(2026, 3, 15))
                .status(InvoiceStatus.PAID)
                .total(new BigDecimal("1000.00"))
                .build();

        Invoice invoice2Client1 = Invoice.builder()
                .id(2L)
                .client(client1)
                .date(LocalDate.of(2026, 6, 15))
                .status(InvoiceStatus.PAID)
                .total(new BigDecimal("500.00"))
                .build();

        Invoice invoice1Client2 = Invoice.builder()
                .id(3L)
                .client(client2)
                .date(LocalDate.of(2026, 4, 15))
                .status(InvoiceStatus.PAID)
                .total(new BigDecimal("2000.00"))
                .build();

        when(invoiceRepo.findAll()).thenReturn(Arrays.asList(invoice1Client1, invoice2Client1, invoice1Client2));

        // When
        var result = dashboardService.getTopClientsByPeriod(startDate, endDate, 10);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Client 2 should be first (higher revenue)
        assertEquals(2L, result.get(0).getClientId());
        assertEquals(0, new BigDecimal("2000.00").compareTo(result.get(0).getTotalRevenue()));
        
        // Client 1 should be second
        assertEquals(1L, result.get(1).getClientId());
        assertEquals(0, new BigDecimal("1500.00").compareTo(result.get(1).getTotalRevenue()));
        assertEquals(2L, result.get(1).getInvoiceCount());
    }

    @Test
    void testGetTopClientsByPeriod_WithLimit() {
        // Given
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);

        Client client1 = Client.builder().id(1L).companyName("Client 1").build();
        Client client2 = Client.builder().id(2L).companyName("Client 2").build();
        Client client3 = Client.builder().id(3L).companyName("Client 3").build();

        Invoice inv1 = Invoice.builder().id(1L).client(client1).date(LocalDate.of(2026, 1, 1))
                .status(InvoiceStatus.PAID).total(new BigDecimal("3000.00")).build();
        Invoice inv2 = Invoice.builder().id(2L).client(client2).date(LocalDate.of(2026, 1, 1))
                .status(InvoiceStatus.PAID).total(new BigDecimal("2000.00")).build();
        Invoice inv3 = Invoice.builder().id(3L).client(client3).date(LocalDate.of(2026, 1, 1))
                .status(InvoiceStatus.PAID).total(new BigDecimal("1000.00")).build();

        when(invoiceRepo.findAll()).thenReturn(Arrays.asList(inv1, inv2, inv3));

        // When
        var result = dashboardService.getTopClientsByPeriod(startDate, endDate, 2);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Limited to 2
        assertEquals(1L, result.get(0).getClientId()); // Client 1 first (highest revenue)
        assertEquals(2L, result.get(1).getClientId()); // Client 2 second
    }
}

