package com.gp_dev.erp_lite.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp_dev.erp_lite.dtos.ProjectDto;
import com.gp_dev.erp_lite.models.*;
import com.gp_dev.erp_lite.repositories.*;
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
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private UserRepo userRepo;

    private Project testProject;
    private Client testClient;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up first
        projectRepo.deleteAll();
        
        // Get or create test user
        testUser = userRepo.findByEmail("admin@test.com")
                .orElseGet(() -> userRepo.save(User.builder()
                        .email("admin@test.com")
                        .password("password")
                        .firstName("Admin")
                        .lastName("User")
                        .active(true)
                        .build()));

        // Create test client
        testClient = clientRepo.save(Client.builder()
                .companyName("Test Client Company")
                .email("client@test.com")
                .phone("0123456789")
                .build());

        // Create test project
        testProject = projectRepo.save(Project.builder()
                .reference("CHANT-TEST-0001")
                .name("Test Project")
                .description("Test Description")
                .client(testClient)
                .createdBy(testUser)
                .status(ProjectStatus.DRAFT)
                .siteAddress("123 Test Street")
                .siteCity("Paris")
                .sitePostalCode("75001")
                .startDate(LocalDate.now().plusDays(7))
                .endDate(LocalDate.now().plusMonths(3))
                .estimatedBudget(new BigDecimal("50000.00"))
                .progressPercentage(0)
                .build());
    }

    @AfterEach
    void tearDown() {
        projectRepo.deleteAll();
        clientRepo.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetAllProjects() throws Exception {
        mockMvc.perform(get("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].reference").value("CHANT-TEST-0001"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetProjectById() throws Exception {
        mockMvc.perform(get("/api/v1/projects/" + testProject.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("CHANT-TEST-0001"))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void testCreateProject() throws Exception {
        ProjectDto newProject = ProjectDto.builder()
                .name("New Project")
                .description("New Project Description")
                .clientId(testClient.getId())
                .createdById(testUser.getId())
                .status(ProjectStatus.PLANNING)
                .siteCity("Lyon")
                .build();

        mockMvc.perform(post("/api/v1/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Project"))
                .andExpect(jsonPath("$.reference").exists());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void testUpdateProject() throws Exception {
        ProjectDto updateDto = ProjectDto.builder()
                .name("Updated Project Name")
                .description("Updated Description")
                .build();

        mockMvc.perform(put("/api/v1/projects/" + testProject.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Project Name"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteProject() throws Exception {
        mockMvc.perform(delete("/api/v1/projects/" + testProject.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertFalse(projectRepo.existsById(testProject.getId()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void testUpdateProjectStatus() throws Exception {
        mockMvc.perform(patch("/api/v1/projects/" + testProject.getId() + "/status")
                        .with(csrf())
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void testUpdateProjectProgress() throws Exception {
        mockMvc.perform(patch("/api/v1/projects/" + testProject.getId() + "/progress")
                        .with(csrf())
                        .param("progressPercentage", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercentage").value(50));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testSearchProjects() throws Exception {
        mockMvc.perform(get("/api/v1/projects/search")
                        .param("query", "Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Project"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetProjectsByStatus() throws Exception {
        mockMvc.perform(get("/api/v1/projects/status/DRAFT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("DRAFT"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void testGetProjectStats() throws Exception {
        mockMvc.perform(get("/api/v1/projects/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.draft").exists())
                .andExpect(jsonPath("$.inProgress").exists())
                .andExpect(jsonPath("$.completed").exists());
    }
}

