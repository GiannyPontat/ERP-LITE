package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.ClientDto;
import com.gp_dev.erp_lite.dtos.InvoiceDto;
import com.gp_dev.erp_lite.dtos.InvoiceItemDto;
import com.gp_dev.erp_lite.dtos.QuoteDto;
import com.gp_dev.erp_lite.dtos.QuoteItemDto;
import com.gp_dev.erp_lite.models.InvoiceStatus;
import com.gp_dev.erp_lite.models.QuoteStatus;
import com.gp_dev.erp_lite.services.impl.PdfServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private PdfServiceImpl pdfService;

    private ClientDto testClient;
    private QuoteDto testQuoteDto;
    private InvoiceDto testInvoiceDto;

    @BeforeEach
    void setUp() {
        // Créer un client de test
        testClient = ClientDto.builder()
                .id(1L)
                .companyName("Test Company")
                .contactFirstName("John")
                .contactLastName("Doe")
                .email("john.doe@test.com")
                .phone("0123456789")
                .address("123 Test Street")
                .city("Paris")
                .postalCode("75001")
                .build();

        // Créer des items de test
        QuoteItemDto item1 = QuoteItemDto.builder()
                .id(1L)
                .description("Service 1")
                .quantity(10)
                .unitPrice(new BigDecimal("100.00"))
                .total(new BigDecimal("1000.00"))
                .build();

        QuoteItemDto item2 = QuoteItemDto.builder()
                .id(2L)
                .description("Service 2")
                .quantity(5)
                .unitPrice(new BigDecimal("50.00"))
                .total(new BigDecimal("250.00"))
                .build();

        // Créer un devis de test
        testQuoteDto = QuoteDto.builder()
                .id(1L)
                .quoteNumber("DEV-2026-0001")
                .clientId(1L)
                .date(LocalDate.now())
                .validUntil(LocalDate.now().plusDays(30))
                .status(QuoteStatus.DRAFT)
                .subtotal(new BigDecimal("1250.00"))
                .taxRate(new BigDecimal("20.00"))
                .taxAmount(new BigDecimal("250.00"))
                .total(new BigDecimal("1500.00"))
                .items(Arrays.asList(item1, item2))
                .build();

        // Créer des items de facture de test
        InvoiceItemDto invoiceItem1 = InvoiceItemDto.builder()
                .id(1L)
                .description("Service 1")
                .quantity(10)
                .unitPrice(new BigDecimal("100.00"))
                .total(new BigDecimal("1000.00"))
                .build();

        InvoiceItemDto invoiceItem2 = InvoiceItemDto.builder()
                .id(2L)
                .description("Service 2")
                .quantity(5)
                .unitPrice(new BigDecimal("50.00"))
                .total(new BigDecimal("250.00"))
                .build();

        // Créer une facture de test
        testInvoiceDto = InvoiceDto.builder()
                .id(1L)
                .invoiceNumber("FACT-2026-0001")
                .clientId(1L)
                .date(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(15))
                .status(InvoiceStatus.SENT)
                .subtotal(new BigDecimal("1250.00"))
                .taxRate(new BigDecimal("20.00"))
                .taxAmount(new BigDecimal("250.00"))
                .total(new BigDecimal("1500.00"))
                .items(Arrays.asList(invoiceItem1, invoiceItem2))
                .build();
    }

    @Test
    void testGenerateQuotePdf_Success() {
        // Given
        when(clientService.findById(anyLong())).thenReturn(testClient);

        // When
        byte[] pdfBytes = pdfService.generateQuotePdf(testQuoteDto);

        // Then
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        // Vérifier que c'est bien un PDF (commence par %PDF)
        String pdfHeader = new String(pdfBytes, 0, Math.min(4, pdfBytes.length));
        assertEquals("%PDF", pdfHeader);
    }

    @Test
    void testGenerateQuotePdf_WithNullItems() {
        // Given
        testQuoteDto.setItems(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> pdfService.generateQuotePdf(testQuoteDto));
    }

    @Test
    void testGenerateInvoicePdf_Success() {
        // Given
        when(clientService.findById(anyLong())).thenReturn(testClient);

        // When
        byte[] pdfBytes = pdfService.generateInvoicePdf(testInvoiceDto);

        // Then
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        // Vérifier que c'est bien un PDF (commence par %PDF)
        String pdfHeader = new String(pdfBytes, 0, Math.min(4, pdfBytes.length));
        assertEquals("%PDF", pdfHeader);
    }

    @Test
    void testGenerateInvoicePdf_WithPaidStatus() {
        // Given
        testInvoiceDto.setStatus(InvoiceStatus.PAID);
        testInvoiceDto.setPaidDate(LocalDate.now());
        when(clientService.findById(anyLong())).thenReturn(testClient);

        // When
        byte[] pdfBytes = pdfService.generateInvoicePdf(testInvoiceDto);

        // Then
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void testGenerateQuotePdf_ClientNotFound() {
        // Given
        when(clientService.findById(anyLong())).thenThrow(new RuntimeException("Client not found"));

        // When & Then
        assertThrows(Exception.class, () -> pdfService.generateQuotePdf(testQuoteDto));
    }

    @Test
    void testGenerateQuotePdf_WithNotesAndTerms() {
        // Given
        testQuoteDto.setNotes("Notes de test");
        testQuoteDto.setTermsAndConditions("Conditions générales de test");
        when(clientService.findById(anyLong())).thenReturn(testClient);

        // When
        byte[] pdfBytes = pdfService.generateQuotePdf(testQuoteDto);

        // Then
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }
}

