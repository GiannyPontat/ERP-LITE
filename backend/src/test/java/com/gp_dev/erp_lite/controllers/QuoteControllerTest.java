package com.gp_dev.erp_lite.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp_dev.erp_lite.config.TestSecurityConfig;
import com.gp_dev.erp_lite.dtos.QuoteDto;
import com.gp_dev.erp_lite.models.QuoteStatus;
import com.gp_dev.erp_lite.services.PdfService;
import com.gp_dev.erp_lite.services.QuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuoteController.class)
@Import(TestSecurityConfig.class)
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuoteService quoteService;

    @MockBean
    private PdfService pdfService;

    @MockBean
    private com.gp_dev.erp_lite.services.EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    private QuoteDto quoteDto;

    @BeforeEach
    void setUp() {
        quoteDto = QuoteDto.builder()
                .id(1L)
                .quoteNumber("DEV-2026-0001")
                .clientId(1L)
                .clientName("Test Company")
                .createdById(1L)
                .date(LocalDate.now())
                .validUntil(LocalDate.now().plusDays(30))
                .status(QuoteStatus.DRAFT)
                .subtotal(new BigDecimal("1000.00"))
                .taxRate(new BigDecimal("20.00"))
                .taxAmount(new BigDecimal("200.00"))
                .total(new BigDecimal("1200.00"))
                .build();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetAllQuotes_Success() throws Exception {
        // Given
        List<QuoteDto> quotes = Arrays.asList(quoteDto);
        when(quoteService.findAll()).thenReturn(quotes);

        // When & Then
        mockMvc.perform(get("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].quoteNumber").value("DEV-2026-0001"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetQuoteById_Success() throws Exception {
        // Given
        when(quoteService.findById(1L)).thenReturn(quoteDto);

        // When & Then
        mockMvc.perform(get("/api/v1/quotes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quoteNumber").value("DEV-2026-0001"))
                .andExpect(jsonPath("$.total").value(1200.00));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateQuote_Success() throws Exception {
        // Given
        QuoteDto newQuoteDto = QuoteDto.builder()
                .clientId(1L)
                .createdById(1L)
                .date(LocalDate.now())
                .status(QuoteStatus.DRAFT)
                .subtotal(new BigDecimal("1000.00"))
                .taxRate(new BigDecimal("20.00"))
                .taxAmount(new BigDecimal("200.00"))
                .total(new BigDecimal("1200.00"))
                .build();

        when(quoteService.create(any(QuoteDto.class))).thenReturn(quoteDto);

        // When & Then
        mockMvc.perform(post("/api/v1/quotes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuoteDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quoteNumber").exists());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testCreateQuote_Forbidden() throws Exception {
        // Given
        QuoteDto newQuoteDto = QuoteDto.builder()
                .clientId(1L)
                .createdById(1L)
                .date(LocalDate.now())
                .status(QuoteStatus.DRAFT)
                .subtotal(new BigDecimal("1000.00"))
                .taxRate(new BigDecimal("20.00"))
                .taxAmount(new BigDecimal("200.00"))
                .total(new BigDecimal("1200.00"))
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/quotes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuoteDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateQuote_Success() throws Exception {
        // Given
        quoteDto.setNotes("Updated notes");
        when(quoteService.update(anyLong(), any(QuoteDto.class))).thenReturn(quoteDto);

        // When & Then
        mockMvc.perform(put("/api/v1/quotes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quoteDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quoteNumber").value("DEV-2026-0001"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteQuote_Success() throws Exception {
        // Given
        doNothing().when(quoteService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/quotes/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGeneratePdf_Success() throws Exception {
        // Given
        when(quoteService.findById(1L)).thenReturn(quoteDto);
        byte[] pdfBytes = "%PDF-1.5 test content".getBytes();
        when(pdfService.generateQuotePdf(any(QuoteDto.class))).thenReturn(pdfBytes);

        // When & Then
        mockMvc.perform(get("/api/v1/quotes/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"devis-DEV-2026-0001.pdf\""))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    void testGetAllQuotes_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/quotes"))
                .andExpect(status().isForbidden());
    }
}

