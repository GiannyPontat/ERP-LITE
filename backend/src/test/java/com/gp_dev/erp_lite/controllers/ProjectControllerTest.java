package com.gp_dev.erp_lite.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp_dev.erp_lite.config.TestSecurityConfig;
import com.gp_dev.erp_lite.dtos.ProjectDto;
import com.gp_dev.erp_lite.models.ProjectStatus;
import com.gp_dev.erp_lite.services.ProjectService;
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
import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@Import(TestSecurityConfig.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .id(1L)
                .reference("CHANT-2026-0001")
                .name("Rénovation appartement")
                .description("Rénovation complète d'un appartement T3")
                .clientId(1L)
                .clientName("Test Company")
                .createdById(1L)
                .createdByName("John Doe")
                .status(ProjectStatus.DRAFT)
                .statusDisplayName("Brouillon")
                .siteAddress("123 Rue Test")
                .siteCity("Paris")
                .sitePostalCode("75001")
                .startDate(LocalDate.now().plusDays(7))
                .endDate(LocalDate.now().plusMonths(3))
                .estimatedBudget(new BigDecimal("50000.00"))
                .progressPercentage(0)
                .quotesCount(0)
                .invoicesCount(0)
                .documentsCount(0)
                .build();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetAll_Success() throws Exception {
        // Given
        Page<ProjectDto> page = new PageImpl<>(Arrays.asList(projectDto));
        when(projectService.findAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].reference").value("CHANT-2026-0001"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetById_Success() throws Exception {
        // Given
        when(projectService.findById(1L)).thenReturn(projectDto);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("CHANT-2026-0001"))
                .andExpect(jsonPath("$.name").value("Rénovation appartement"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void testCreate_Success() throws Exception {
        // Given
        ProjectDto newProject = ProjectDto.builder()
                .name("Nouveau chantier")
                .clientId(1L)
                .createdById(1L)
                .build();

        when(projectService.create(any(ProjectDto.class))).thenReturn(projectDto);

        // When & Then
        mockMvc.perform(post("/api/v1/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").exists());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testCreate_Forbidden() throws Exception {
        // Given
        ProjectDto newProject = ProjectDto.builder()
                .name("Nouveau chantier")
                .clientId(1L)
                .createdById(1L)
                .build();

        // When & Then - USER cannot create projects
        mockMvc.perform(post("/api/v1/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void testUpdate_Success() throws Exception {
        // Given
        when(projectService.update(anyLong(), any(ProjectDto.class))).thenReturn(projectDto);

        // When & Then
        mockMvc.perform(put("/api/v1/projects/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("CHANT-2026-0001"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDelete_Success() throws Exception {
        // Given
        doNothing().when(projectService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/projects/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void testDelete_Forbidden() throws Exception {
        // When & Then - MANAGER cannot delete projects (only ADMIN)
        mockMvc.perform(delete("/api/v1/projects/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void testUpdateStatus_Success() throws Exception {
        // Given
        when(projectService.updateStatus(1L, ProjectStatus.IN_PROGRESS)).thenReturn(projectDto);

        // When & Then
        mockMvc.perform(patch("/api/v1/projects/1/status")
                        .with(csrf())
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void testUpdateProgress_Success() throws Exception {
        // Given
        when(projectService.updateProgress(1L, 50)).thenReturn(projectDto);

        // When & Then
        mockMvc.perform(patch("/api/v1/projects/1/progress")
                        .with(csrf())
                        .param("progressPercentage", "50"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testSearch_Success() throws Exception {
        // Given
        Page<ProjectDto> page = new PageImpl<>(Arrays.asList(projectDto));
        when(projectService.search(any(), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/search")
                        .param("query", "rénovation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void testGetStats_Success() throws Exception {
        // Given
        when(projectService.countByStatus(any(ProjectStatus.class))).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.draft").exists())
                .andExpect(jsonPath("$.inProgress").exists());
    }

    @Test
    void testGetAll_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isForbidden());
    }
}

