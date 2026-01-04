package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.ClientDto;
import com.gp_dev.erp_lite.dtos.CreateClientDto;
import com.gp_dev.erp_lite.dtos.UpdateClientDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.Client;
import com.gp_dev.erp_lite.repositories.ClientRepo;
import com.gp_dev.erp_lite.services.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepo clientRepo;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private ClientDto clientDto;
    private CreateClientDto createClientDto;

    @BeforeEach
    void setUp() {
        client = Client.builder()
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

        clientDto = ClientDto.builder()
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

        createClientDto = CreateClientDto.builder()
                .companyName("Test Company")
                .contactFirstName("John")
                .contactLastName("Doe")
                .email("john.doe@test.com")
                .phone("0123456789")
                .address("123 Test Street")
                .city("Paris")
                .postalCode("75001")
                .build();
    }

    @Test
    void testFindAll_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> clientPage = new PageImpl<>(Arrays.asList(client));
        when(clientRepo.findAll(pageable)).thenReturn(clientPage);

        // When
        Page<ClientDto> result = clientService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Company", result.getContent().get(0).getCompanyName());
        verify(clientRepo, times(1)).findAll(pageable);
    }

    @Test
    void testFindById_Success() {
        // Given
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));

        // When
        ClientDto result = clientService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Company", result.getCompanyName());
        assertEquals("john.doe@test.com", result.getEmail());
        verify(clientRepo, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        when(clientRepo.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> clientService.findById(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Client not found"));
    }

    @Test
    void testCreate_Success() {
        // Given
        when(clientRepo.save(any(Client.class))).thenReturn(client);

        // When
        ClientDto result = clientService.create(createClientDto);

        // Then
        assertNotNull(result);
        assertEquals("Test Company", result.getCompanyName());
        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepo, times(1)).save(clientCaptor.capture());
        Client savedClient = clientCaptor.getValue();
        assertEquals("Test Company", savedClient.getCompanyName());
        assertEquals("john.doe@test.com", savedClient.getEmail());
    }

    @Test
    void testUpdate_Success() {
        // Given
        UpdateClientDto updateDto = UpdateClientDto.builder()
                .companyName("Updated Company")
                .email("updated@test.com")
                .build();

        Client existingClient = Client.builder()
                .id(1L)
                .companyName("Test Company")
                .email("john.doe@test.com")
                .build();

        when(clientRepo.findById(1L)).thenReturn(Optional.of(existingClient));
        when(clientRepo.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ClientDto result = clientService.update(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(clientRepo, times(1)).findById(1L);
        verify(clientRepo, times(1)).save(any(Client.class));
    }

    @Test
    void testUpdate_NotFound() {
        // Given
        UpdateClientDto updateDto = UpdateClientDto.builder().companyName("Updated").build();
        when(clientRepo.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> clientService.update(1L, updateDto));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testDelete_Success() {
        // Given
        when(clientRepo.existsById(1L)).thenReturn(true);
        doNothing().when(clientRepo).deleteById(1L);

        // When
        clientService.delete(1L);

        // Then
        verify(clientRepo, times(1)).existsById(1L);
        verify(clientRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        // Given
        when(clientRepo.existsById(anyLong())).thenReturn(false);

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> clientService.delete(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testSearch_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> clientPage = new PageImpl<>(Arrays.asList(client));
        // Mock de la méthode findBySearchTerm
        when(clientRepo.findBySearchTerm(anyString(), any(Pageable.class))).thenReturn(clientPage);

        // When
        Page<ClientDto> result = clientService.search("Test", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(clientRepo, times(1)).findBySearchTerm("Test", pageable);
    }
}

