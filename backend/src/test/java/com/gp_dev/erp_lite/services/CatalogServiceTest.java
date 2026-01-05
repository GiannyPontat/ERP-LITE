package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.CatalogItemDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.CatalogCategory;
import com.gp_dev.erp_lite.models.CatalogItem;
import com.gp_dev.erp_lite.repositories.CatalogItemRepo;
import com.gp_dev.erp_lite.services.impl.CatalogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private CatalogItemRepo catalogItemRepo;

    @InjectMocks
    private CatalogServiceImpl catalogService;

    private CatalogItem catalogItem;
    private CatalogItemDto catalogItemDto;

    @BeforeEach
    void setUp() {
        catalogItem = CatalogItem.builder()
                .id(1L)
                .reference("ELEC-001")
                .designation("Prise électrique 2P+T")
                .description("Prise électrique encastrable")
                .category(CatalogCategory.ELECTRICITE)
                .unit("u")
                .unitPrice(new BigDecimal("12.50"))
                .taxRate(new BigDecimal("20.00"))
                .costPrice(new BigDecimal("8.00"))
                .active(true)
                .build();

        catalogItemDto = CatalogItemDto.builder()
                .reference("ELEC-001")
                .designation("Prise électrique 2P+T")
                .description("Prise électrique encastrable")
                .category(CatalogCategory.ELECTRICITE)
                .unit("u")
                .unitPrice(new BigDecimal("12.50"))
                .taxRate(new BigDecimal("20.00"))
                .costPrice(new BigDecimal("8.00"))
                .active(true)
                .build();
    }

    @Test
    void testFindAll_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<CatalogItem> page = new PageImpl<>(Arrays.asList(catalogItem));
        when(catalogItemRepo.findAll(pageable)).thenReturn(page);

        // When
        Page<CatalogItemDto> result = catalogService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("ELEC-001", result.getContent().get(0).getReference());
        verify(catalogItemRepo, times(1)).findAll(pageable);
    }

    @Test
    void testFindById_Success() {
        // Given
        when(catalogItemRepo.findById(1L)).thenReturn(Optional.of(catalogItem));

        // When
        CatalogItemDto result = catalogService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals("ELEC-001", result.getReference());
        assertEquals("Prise électrique 2P+T", result.getDesignation());
        verify(catalogItemRepo, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        when(catalogItemRepo.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> catalogService.findById(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testCreate_Success() {
        // Given
        when(catalogItemRepo.existsByReference(anyString())).thenReturn(false);
        when(catalogItemRepo.save(any(CatalogItem.class))).thenReturn(catalogItem);

        // When
        CatalogItemDto result = catalogService.create(catalogItemDto);

        // Then
        assertNotNull(result);
        assertEquals("ELEC-001", result.getReference());
        verify(catalogItemRepo, times(1)).save(any(CatalogItem.class));
    }

    @Test
    void testCreate_DuplicateReference() {
        // Given
        when(catalogItemRepo.existsByReference(anyString())).thenReturn(true);

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> catalogService.create(catalogItemDto));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void testUpdate_Success() {
        // Given
        CatalogItemDto updateDto = CatalogItemDto.builder()
                .designation("Prise électrique mise à jour")
                .unitPrice(new BigDecimal("15.00"))
                .build();

        when(catalogItemRepo.findById(1L)).thenReturn(Optional.of(catalogItem));
        when(catalogItemRepo.save(any(CatalogItem.class))).thenReturn(catalogItem);

        // When
        CatalogItemDto result = catalogService.update(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(catalogItemRepo, times(1)).findById(1L);
        verify(catalogItemRepo, times(1)).save(any(CatalogItem.class));
    }

    @Test
    void testDelete_Success() {
        // Given
        when(catalogItemRepo.existsById(1L)).thenReturn(true);
        doNothing().when(catalogItemRepo).deleteById(1L);

        // When
        catalogService.delete(1L);

        // Then
        verify(catalogItemRepo, times(1)).existsById(1L);
        verify(catalogItemRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        // Given
        when(catalogItemRepo.existsById(anyLong())).thenReturn(false);

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> catalogService.delete(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testToggleActive_Success() {
        // Given
        when(catalogItemRepo.findById(1L)).thenReturn(Optional.of(catalogItem));
        when(catalogItemRepo.save(any(CatalogItem.class))).thenReturn(catalogItem);

        // When
        catalogService.toggleActive(1L);

        // Then
        verify(catalogItemRepo, times(1)).findById(1L);
        verify(catalogItemRepo, times(1)).save(any(CatalogItem.class));
    }

    @Test
    void testSearchActive_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<CatalogItem> page = new PageImpl<>(Arrays.asList(catalogItem));
        when(catalogItemRepo.searchActive(anyString(), any(Pageable.class))).thenReturn(page);

        // When
        Page<CatalogItemDto> result = catalogService.searchActive("prise", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(catalogItemRepo, times(1)).searchActive("prise", pageable);
    }

    @Test
    void testFindByCategory_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<CatalogItem> page = new PageImpl<>(Arrays.asList(catalogItem));
        when(catalogItemRepo.findByCategory(CatalogCategory.ELECTRICITE, pageable)).thenReturn(page);

        // When
        Page<CatalogItemDto> result = catalogService.findByCategory(CatalogCategory.ELECTRICITE, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(catalogItemRepo, times(1)).findByCategory(CatalogCategory.ELECTRICITE, pageable);
    }

    @Test
    void testGetAllCategories() {
        // When
        var categories = catalogService.getAllCategories();

        // Then
        assertNotNull(categories);
        assertTrue(categories.contains(CatalogCategory.ELECTRICITE));
        assertTrue(categories.contains(CatalogCategory.PLOMBERIE));
    }

    @Test
    void testMarginCalculation() {
        // Given
        when(catalogItemRepo.findById(1L)).thenReturn(Optional.of(catalogItem));

        // When
        CatalogItemDto result = catalogService.findById(1L);

        // Then
        assertNotNull(result.getMargin());
        assertEquals(0, new BigDecimal("4.50").compareTo(result.getMargin())); // 12.50 - 8.00
        assertNotNull(result.getMarginPercentage());
        assertEquals(0, new BigDecimal("56.25").compareTo(result.getMarginPercentage())); // (4.50 / 8.00) * 100
    }
}

