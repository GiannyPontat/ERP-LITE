package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.InvoiceDto;
import com.gp_dev.erp_lite.dtos.InvoiceItemDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.*;
import com.gp_dev.erp_lite.repositories.*;
import com.gp_dev.erp_lite.services.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class InvoiceServiceTest {

    @Mock
    private InvoiceRepo invoiceRepo;

    @Mock
    private InvoiceItemRepo invoiceItemRepo;

    @Mock
    private QuoteRepo quoteRepo;

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private NumberGeneratorService numberGeneratorService;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private InvoiceDto invoiceDto;
    private Client client;
    private User user;
    private Invoice savedInvoice;

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

        InvoiceItemDto item1 = InvoiceItemDto.builder()
                .description("Service 1")
                .quantity(10)
                .unitPrice(new BigDecimal("100.00"))
                .build();

        InvoiceItemDto item2 = InvoiceItemDto.builder()
                .description("Service 2")
                .quantity(5)
                .unitPrice(new BigDecimal("50.00"))
                .build();

        invoiceDto = InvoiceDto.builder()
                .clientId(1L)
                .createdById(1L)
                .date(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(15))
                .status(InvoiceStatus.SENT)
                .taxRate(new BigDecimal("20.00"))
                .items(Arrays.asList(item1, item2))
                .build();

        savedInvoice = Invoice.builder()
                .id(1L)
                .invoiceNumber("FACT-2026-0001")
                .client(client)
                .createdBy(user)
                .date(LocalDate.now())
                .status(InvoiceStatus.SENT)
                .build();
    }

    @Test
    void testCreateInvoice_Success() {
        // Given
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(numberGeneratorService.generateInvoiceNumber()).thenReturn("FACT-2026-0001");
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(savedInvoice);
        when(invoiceItemRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InvoiceDto result = invoiceService.create(invoiceDto);

        // Then
        assertNotNull(result);
        verify(clientRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).findById(1L);
        verify(invoiceRepo, atLeastOnce()).save(any(Invoice.class));
        verify(invoiceItemRepo, times(2)).save(any());
    }

    @Test
    void testCreateInvoice_ClientNotFound() {
        // Given
        when(clientRepo.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> invoiceService.create(invoiceDto));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Client not found"));
    }

    @Test
    void testFindById_Success() {
        // Given
        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(savedInvoice));

        // When
        InvoiceDto result = invoiceService.findById(1L);

        // Then
        assertNotNull(result);
        verify(invoiceRepo, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        when(invoiceRepo.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> invoiceService.findById(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Invoice not found"));
    }

    @Test
    void testDelete_Success() {
        // Given
        when(invoiceRepo.existsById(1L)).thenReturn(true);
        doNothing().when(invoiceRepo).deleteById(1L);

        // When
        invoiceService.delete(1L);

        // Then
        verify(invoiceRepo, times(1)).existsById(1L);
        verify(invoiceRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        // Given
        when(invoiceRepo.existsById(anyLong())).thenReturn(false);

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> invoiceService.delete(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}

