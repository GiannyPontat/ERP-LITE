package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.QuoteDto;
import com.gp_dev.erp_lite.dtos.QuoteItemDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.Client;
import com.gp_dev.erp_lite.models.Quote;
import com.gp_dev.erp_lite.models.QuoteStatus;
import com.gp_dev.erp_lite.models.User;
import com.gp_dev.erp_lite.repositories.ClientRepo;
import com.gp_dev.erp_lite.repositories.QuoteItemRepo;
import com.gp_dev.erp_lite.repositories.QuoteRepo;
import com.gp_dev.erp_lite.repositories.UserRepo;
import com.gp_dev.erp_lite.services.impl.QuoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteRepo quoteRepo;

    @Mock
    private QuoteItemRepo quoteItemRepo;

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private NumberGeneratorService numberGeneratorService;

    @InjectMocks
    private QuoteServiceImpl quoteService;

    private QuoteDto quoteDto;
    private Client client;
    private User user;
    private Quote savedQuote;

    @BeforeEach
    void setUp() {
        // Créer un client
        client = Client.builder()
                .id(1L)
                .companyName("Test Company")
                .email("test@company.com")
                .build();

        // Créer un utilisateur
        user = User.builder()
                .id(1L)
                .email("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        // Créer des items de devis
        QuoteItemDto item1 = QuoteItemDto.builder()
                .description("Service 1")
                .quantity(10)
                .unitPrice(new BigDecimal("100.00"))
                .build();

        QuoteItemDto item2 = QuoteItemDto.builder()
                .description("Service 2")
                .quantity(5)
                .unitPrice(new BigDecimal("50.00"))
                .build();

        // Créer un DTO de devis
        quoteDto = QuoteDto.builder()
                .clientId(1L)
                .createdById(1L)
                .date(LocalDate.now())
                .validUntil(LocalDate.now().plusDays(30))
                .status(QuoteStatus.DRAFT)
                .taxRate(new BigDecimal("20.00"))
                .items(Arrays.asList(item1, item2))
                .build();

        // Créer un devis sauvegardé
        savedQuote = Quote.builder()
                .id(1L)
                .quoteNumber("DEV-2026-0001")
                .client(client)
                .createdBy(user)
                .date(LocalDate.now())
                .status(QuoteStatus.DRAFT)
                .build();
    }

    @Test
    void testCreateQuote_Success() {
        // Given
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(numberGeneratorService.generateQuoteNumber()).thenReturn("DEV-2026-0001");
        when(quoteRepo.save(any(Quote.class))).thenReturn(savedQuote);
        when(quoteItemRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        QuoteDto result = quoteService.create(quoteDto);

        // Then
        assertNotNull(result);
        verify(clientRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).findById(1L);
        verify(quoteRepo, atLeastOnce()).save(any(Quote.class));
        verify(quoteItemRepo, times(2)).save(any()); // 2 items
    }

    @Test
    void testCreateQuote_ClientNotFound() {
        // Given
        when(clientRepo.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> quoteService.create(quoteDto));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Client not found"));
    }

    @Test
    void testCreateQuote_UserNotFound() {
        // Given
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> quoteService.create(quoteDto));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void testCreateQuote_CalculatesTotalsCorrectly() {
        // Given
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(numberGeneratorService.generateQuoteNumber()).thenReturn("DEV-2026-0001");
        when(quoteItemRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Quote> quoteCaptor = ArgumentCaptor.forClass(Quote.class);
        when(quoteRepo.save(quoteCaptor.capture())).thenReturn(savedQuote);

        // When
        quoteService.create(quoteDto);

        // Then
        Quote savedQuote = quoteCaptor.getValue();
        // Subtotal = (10 * 100) + (5 * 50) = 1000 + 250 = 1250
        assertEquals(0, new BigDecimal("1250.00").compareTo(savedQuote.getSubtotal()));
        // Tax amount = 1250 * 20 / 100 = 250
        assertEquals(0, new BigDecimal("250.00").compareTo(savedQuote.getTaxAmount()));
        // Total = 1250 + 250 = 1500
        assertEquals(0, new BigDecimal("1500.00").compareTo(savedQuote.getTotal()));
    }

    @Test
    void testFindById_Success() {
        // Given
        when(quoteRepo.findById(1L)).thenReturn(Optional.of(savedQuote));

        // When
        QuoteDto result = quoteService.findById(1L);

        // Then
        assertNotNull(result);
        verify(quoteRepo, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        when(quoteRepo.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> quoteService.findById(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Quote not found"));
    }

    @Test
    void testDelete_Success() {
        // Given
        when(quoteRepo.existsById(1L)).thenReturn(true);
        doNothing().when(quoteRepo).deleteById(1L);

        // When
        quoteService.delete(1L);

        // Then
        verify(quoteRepo, times(1)).existsById(1L);
        verify(quoteRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        // Given
        when(quoteRepo.existsById(anyLong())).thenReturn(false);

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> quoteService.delete(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Quote not found"));
    }
}

