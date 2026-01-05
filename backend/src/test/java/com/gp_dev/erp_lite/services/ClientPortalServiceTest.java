package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.ClientPortalAuthResponse;
import com.gp_dev.erp_lite.dtos.ClientPortalInvoiceDto;
import com.gp_dev.erp_lite.dtos.ClientPortalLoginRequest;
import com.gp_dev.erp_lite.dtos.ClientPortalQuoteDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.*;
import com.gp_dev.erp_lite.repositories.*;
import com.gp_dev.erp_lite.security.JwtUtil;
import com.gp_dev.erp_lite.services.impl.ClientPortalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientPortalServiceTest {

    @Mock
    private ClientPortalAccessRepo portalAccessRepo;

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private QuoteRepo quoteRepo;

    @Mock
    private InvoiceRepo invoiceRepo;

    @Mock
    private ProjectRepo projectRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PdfService pdfService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ClientPortalServiceImpl clientPortalService;

    private Client client;
    private User user;
    private ClientPortalAccess portalAccess;
    private Quote quote;
    private Invoice invoice;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id(1L)
                .companyName("Test Company")
                .email("test@company.com")
                .build();

        user = User.builder()
                .id(1L)
                .email("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        portalAccess = ClientPortalAccess.builder()
                .id(1L)
                .client(client)
                .email("portal@test.com")
                .password("hashedPassword")
                .active(true)
                .emailVerified(true)
                .build();

        quote = Quote.builder()
                .id(1L)
                .quoteNumber("DEV-2026-0001")
                .client(client)
                .createdBy(user)
                .date(LocalDate.now())
                .status(QuoteStatus.SENT)
                .subtotal(new BigDecimal("1000.00"))
                .taxRate(new BigDecimal("20.00"))
                .taxAmount(new BigDecimal("200.00"))
                .total(new BigDecimal("1200.00"))
                .items(Collections.emptyList())
                .build();

        invoice = Invoice.builder()
                .id(1L)
                .invoiceNumber("FACT-2026-0001")
                .client(client)
                .createdBy(user)
                .date(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .status(InvoiceStatus.SENT)
                .subtotal(new BigDecimal("1000.00"))
                .taxRate(new BigDecimal("20.00"))
                .taxAmount(new BigDecimal("200.00"))
                .total(new BigDecimal("1200.00"))
                .items(Collections.emptyList())
                .build();
    }

    @Test
    void testLogin_Success() {
        // Given
        ClientPortalLoginRequest request = new ClientPortalLoginRequest("portal@test.com", "password123");
        
        when(portalAccessRepo.findByEmailAndActiveTrue("portal@test.com"))
                .thenReturn(Optional.of(portalAccess));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateClientPortalToken("portal@test.com", 1L)).thenReturn("jwt-token");
        when(portalAccessRepo.save(any(ClientPortalAccess.class))).thenReturn(portalAccess);

        // When
        ClientPortalAuthResponse result = clientPortalService.login(request);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result.getAccessToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals(1L, result.getClientId());
        assertEquals("Test Company", result.getClientName());
    }

    @Test
    void testLogin_InvalidPassword() {
        // Given
        ClientPortalLoginRequest request = new ClientPortalLoginRequest("portal@test.com", "wrongPassword");
        
        when(portalAccessRepo.findByEmailAndActiveTrue("portal@test.com"))
                .thenReturn(Optional.of(portalAccess));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> clientPortalService.login(request));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void testLogin_EmailNotVerified() {
        // Given
        portalAccess.setEmailVerified(false);
        ClientPortalLoginRequest request = new ClientPortalLoginRequest("portal@test.com", "password123");
        
        when(portalAccessRepo.findByEmailAndActiveTrue("portal@test.com"))
                .thenReturn(Optional.of(portalAccess));

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> clientPortalService.login(request));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertTrue(exception.getMessage().contains("Email not verified"));
    }

    @Test
    void testGetClientQuotes_Success() {
        // Given
        when(quoteRepo.findByClientId(1L)).thenReturn(Arrays.asList(quote));

        // When
        List<ClientPortalQuoteDto> result = clientPortalService.getClientQuotes(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("DEV-2026-0001", result.get(0).getQuoteNumber());
        assertTrue(result.get(0).getCanAccept());
        assertTrue(result.get(0).getCanReject());
    }

    @Test
    void testAcceptQuote_Success() {
        // Given
        when(quoteRepo.findById(1L)).thenReturn(Optional.of(quote));
        when(quoteRepo.save(any(Quote.class))).thenReturn(quote);

        // When
        ClientPortalQuoteDto result = clientPortalService.acceptQuote(1L, 1L);

        // Then
        assertNotNull(result);
        verify(quoteRepo, times(1)).save(any(Quote.class));
    }

    @Test
    void testAcceptQuote_WrongClient() {
        // Given
        quote.setClient(Client.builder().id(2L).build());
        when(quoteRepo.findById(1L)).thenReturn(Optional.of(quote));

        // When & Then
        AppException exception = assertThrows(AppException.class, 
            () -> clientPortalService.acceptQuote(1L, 1L));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void testAcceptQuote_WrongStatus() {
        // Given
        quote.setStatus(QuoteStatus.DRAFT);
        when(quoteRepo.findById(1L)).thenReturn(Optional.of(quote));

        // When & Then
        AppException exception = assertThrows(AppException.class, 
            () -> clientPortalService.acceptQuote(1L, 1L));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testRejectQuote_Success() {
        // Given
        when(quoteRepo.findById(1L)).thenReturn(Optional.of(quote));
        when(quoteRepo.save(any(Quote.class))).thenReturn(quote);

        // When
        ClientPortalQuoteDto result = clientPortalService.rejectQuote(1L, 1L);

        // Then
        assertNotNull(result);
        verify(quoteRepo, times(1)).save(any(Quote.class));
    }

    @Test
    void testGetClientInvoices_Success() {
        // Given
        when(invoiceRepo.findByClientId(1L)).thenReturn(Arrays.asList(invoice));

        // When
        List<ClientPortalInvoiceDto> result = clientPortalService.getClientInvoices(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("FACT-2026-0001", result.get(0).getInvoiceNumber());
    }

    @Test
    void testGetInvoice_AccessDenied() {
        // Given
        invoice.setClient(Client.builder().id(2L).build());
        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));

        // When & Then
        AppException exception = assertThrows(AppException.class, 
            () -> clientPortalService.getInvoice(1L, 1L));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void testHasPortalAccess_True() {
        // Given
        when(portalAccessRepo.findByClientId(1L)).thenReturn(Arrays.asList(portalAccess));

        // When
        boolean result = clientPortalService.hasPortalAccess(1L);

        // Then
        assertTrue(result);
    }

    @Test
    void testHasPortalAccess_False() {
        // Given
        when(portalAccessRepo.findByClientId(1L)).thenReturn(Collections.emptyList());

        // When
        boolean result = clientPortalService.hasPortalAccess(1L);

        // Then
        assertFalse(result);
    }

    @Test
    void testDisablePortalAccess() {
        // Given
        when(portalAccessRepo.findByClientId(1L)).thenReturn(Arrays.asList(portalAccess));
        when(portalAccessRepo.save(any(ClientPortalAccess.class))).thenReturn(portalAccess);

        // When
        clientPortalService.disablePortalAccess(1L);

        // Then
        verify(portalAccessRepo, times(1)).save(any(ClientPortalAccess.class));
    }
}

