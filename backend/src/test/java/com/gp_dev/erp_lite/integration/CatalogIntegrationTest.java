package com.gp_dev.erp_lite.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp_dev.erp_lite.dtos.CatalogItemDto;
import com.gp_dev.erp_lite.models.CatalogCategory;
import com.gp_dev.erp_lite.models.CatalogItem;
import com.gp_dev.erp_lite.repositories.CatalogItemRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CatalogItemRepo catalogItemRepo;

    private CatalogItem testItem;

    @BeforeEach
    void setUp() {
        testItem = catalogItemRepo.save(CatalogItem.builder()
                .reference("TEST-001")
                .designation("Test Item")
                .description("Test Description")
                .category(CatalogCategory.ELECTRICITE)
                .unit("u")
                .unitPrice(new BigDecimal("10.00"))
                .taxRate(new BigDecimal("20.00"))
                .active(true)
                .build());
    }

    @AfterEach
    void tearDown() {
        catalogItemRepo.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetAllCatalogItems() throws Exception {
        mockMvc.perform(get("/api/v1/catalog")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].reference").value("TEST-001"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetCatalogItemById() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/" + testItem.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("TEST-001"))
                .andExpect(jsonPath("$.designation").value("Test Item"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateCatalogItem() throws Exception {
        CatalogItemDto newItem = CatalogItemDto.builder()
                .reference("TEST-002")
                .designation("New Item")
                .category(CatalogCategory.PLOMBERIE)
                .unit("ml")
                .unitPrice(new BigDecimal("15.50"))
                .build();

        mockMvc.perform(post("/api/v1/catalog")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").value("TEST-002"));

        assertTrue(catalogItemRepo.existsByReference("TEST-002"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateDuplicateReference() throws Exception {
        CatalogItemDto duplicateItem = CatalogItemDto.builder()
                .reference("TEST-001") // Same as existing
                .designation("Duplicate Item")
                .category(CatalogCategory.ELECTRICITE)
                .unit("u")
                .unitPrice(new BigDecimal("10.00"))
                .build();

        mockMvc.perform(post("/api/v1/catalog")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateItem)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateCatalogItem() throws Exception {
        CatalogItemDto updateDto = CatalogItemDto.builder()
                .designation("Updated Item")
                .unitPrice(new BigDecimal("25.00"))
                .build();

        mockMvc.perform(put("/api/v1/catalog/" + testItem.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.designation").value("Updated Item"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteCatalogItem() throws Exception {
        mockMvc.perform(delete("/api/v1/catalog/" + testItem.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertFalse(catalogItemRepo.existsById(testItem.getId()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testSearchCatalogItems() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/search")
                        .param("query", "Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reference").value("TEST-001"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetByCategory() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/category/ELECTRICITE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].category").value("ELECTRICITE"));
    }
}

