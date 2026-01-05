package com.gp_dev.erp_lite.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp_dev.erp_lite.config.TestSecurityConfig;
import com.gp_dev.erp_lite.dtos.CatalogItemDto;
import com.gp_dev.erp_lite.models.CatalogCategory;
import com.gp_dev.erp_lite.services.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogController.class)
@Import(TestSecurityConfig.class)
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogService catalogService;

    @Autowired
    private ObjectMapper objectMapper;

    private CatalogItemDto catalogItemDto;

    @BeforeEach
    void setUp() {
        catalogItemDto = CatalogItemDto.builder()
                .id(1L)
                .reference("ELEC-001")
                .designation("Prise électrique 2P+T")
                .description("Prise électrique encastrable")
                .category(CatalogCategory.ELECTRICITE)
                .categoryDisplayName("Électricité")
                .unit("u")
                .unitPrice(new BigDecimal("12.50"))
                .taxRate(new BigDecimal("20.00"))
                .active(true)
                .build();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetAll_Success() throws Exception {
        // Given
        Page<CatalogItemDto> page = new PageImpl<>(Arrays.asList(catalogItemDto));
        when(catalogService.findAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/catalog")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].reference").value("ELEC-001"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetById_Success() throws Exception {
        // Given
        when(catalogService.findById(1L)).thenReturn(catalogItemDto);

        // When & Then
        mockMvc.perform(get("/api/v1/catalog/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("ELEC-001"))
                .andExpect(jsonPath("$.designation").value("Prise électrique 2P+T"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreate_Success() throws Exception {
        // Given
        CatalogItemDto newItem = CatalogItemDto.builder()
                .reference("ELEC-002")
                .designation("Interrupteur simple")
                .category(CatalogCategory.ELECTRICITE)
                .unit("u")
                .unitPrice(new BigDecimal("8.50"))
                .build();

        when(catalogService.create(any(CatalogItemDto.class))).thenReturn(catalogItemDto);

        // When & Then
        mockMvc.perform(post("/api/v1/catalog")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").exists());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testCreate_Forbidden() throws Exception {
        // Given
        CatalogItemDto newItem = CatalogItemDto.builder()
                .reference("ELEC-002")
                .designation("Interrupteur simple")
                .category(CatalogCategory.ELECTRICITE)
                .unit("u")
                .unitPrice(new BigDecimal("8.50"))
                .build();

        // When & Then - USER cannot create items
        mockMvc.perform(post("/api/v1/catalog")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdate_Success() throws Exception {
        // Given
        when(catalogService.update(anyLong(), any(CatalogItemDto.class))).thenReturn(catalogItemDto);

        // When & Then
        mockMvc.perform(put("/api/v1/catalog/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catalogItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("ELEC-001"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDelete_Success() throws Exception {
        // Given
        doNothing().when(catalogService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/catalog/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testSearch_Success() throws Exception {
        // Given
        Page<CatalogItemDto> page = new PageImpl<>(Arrays.asList(catalogItemDto));
        when(catalogService.searchActive(any(), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/catalog/search")
                        .param("query", "prise")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetByCategory_Success() throws Exception {
        // Given
        Page<CatalogItemDto> page = new PageImpl<>(Arrays.asList(catalogItemDto));
        when(catalogService.findByCategory(any(CatalogCategory.class), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/catalog/category/ELECTRICITE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetAllCategories_Success() throws Exception {
        // Given
        when(catalogService.getAllCategories()).thenReturn(Arrays.asList(CatalogCategory.values()));

        // When & Then
        mockMvc.perform(get("/api/v1/catalog/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetAll_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/catalog"))
                .andExpect(status().isForbidden());
    }
}

