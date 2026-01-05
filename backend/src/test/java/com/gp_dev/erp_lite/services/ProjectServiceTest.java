package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.ProjectDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.*;
import com.gp_dev.erp_lite.repositories.*;
import com.gp_dev.erp_lite.services.impl.ProjectServiceImpl;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepo projectRepo;

    @Mock
    private ProjectDocumentRepo documentRepo;

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectDto projectDto;
    private Client client;
    private User user;

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

        project = Project.builder()
                .id(1L)
                .reference("CHANT-2026-0001")
                .name("Rénovation appartement")
                .description("Rénovation complète d'un appartement T3")
                .client(client)
                .createdBy(user)
                .status(ProjectStatus.DRAFT)
                .siteAddress("123 Rue Test")
                .siteCity("Paris")
                .sitePostalCode("75001")
                .startDate(LocalDate.now().plusDays(7))
                .endDate(LocalDate.now().plusMonths(3))
                .estimatedBudget(new BigDecimal("50000.00"))
                .progressPercentage(0)
                .quotes(Collections.emptyList())
                .invoices(Collections.emptyList())
                .build();

        projectDto = ProjectDto.builder()
                .name("Rénovation appartement")
                .description("Rénovation complète d'un appartement T3")
                .clientId(1L)
                .createdById(1L)
                .status(ProjectStatus.DRAFT)
                .siteAddress("123 Rue Test")
                .siteCity("Paris")
                .sitePostalCode("75001")
                .startDate(LocalDate.now().plusDays(7))
                .endDate(LocalDate.now().plusMonths(3))
                .estimatedBudget(new BigDecimal("50000.00"))
                .build();
    }

    @Test
    void testFindAll_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Project> page = new PageImpl<>(Arrays.asList(project));
        when(projectRepo.findAll(pageable)).thenReturn(page);
        when(documentRepo.countByProjectId(anyLong())).thenReturn(0L);

        // When
        Page<ProjectDto> result = projectService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("CHANT-2026-0001", result.getContent().get(0).getReference());
        verify(projectRepo, times(1)).findAll(pageable);
    }

    @Test
    void testFindById_Success() {
        // Given
        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(documentRepo.countByProjectId(1L)).thenReturn(0L);

        // When
        ProjectDto result = projectService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals("CHANT-2026-0001", result.getReference());
        assertEquals("Rénovation appartement", result.getName());
        verify(projectRepo, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        when(projectRepo.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> projectService.findById(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testCreate_Success() {
        // Given
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(projectRepo.findLastReferenceByPrefix(anyString())).thenReturn(Optional.empty());
        when(projectRepo.save(any(Project.class))).thenReturn(project);
        when(documentRepo.countByProjectId(anyLong())).thenReturn(0L);

        // When
        ProjectDto result = projectService.create(projectDto);

        // Then
        assertNotNull(result);
        verify(clientRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).findById(1L);
        verify(projectRepo, times(1)).save(any(Project.class));
    }

    @Test
    void testCreate_ClientNotFound() {
        // Given
        when(clientRepo.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> projectService.create(projectDto));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("Client not found"));
    }

    @Test
    void testUpdateStatus_Success() {
        // Given
        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepo.save(any(Project.class))).thenReturn(project);
        when(documentRepo.countByProjectId(1L)).thenReturn(0L);

        // When
        ProjectDto result = projectService.updateStatus(1L, ProjectStatus.IN_PROGRESS);

        // Then
        assertNotNull(result);
        verify(projectRepo, times(1)).findById(1L);
        verify(projectRepo, times(1)).save(any(Project.class));
    }

    @Test
    void testUpdateProgress_Success() {
        // Given
        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepo.save(any(Project.class))).thenReturn(project);
        when(documentRepo.countByProjectId(1L)).thenReturn(0L);

        // When
        ProjectDto result = projectService.updateProgress(1L, 50);

        // Then
        assertNotNull(result);
        verify(projectRepo, times(1)).save(any(Project.class));
    }

    @Test
    void testUpdateProgress_InvalidValue() {
        // Given
        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));

        // When & Then
        AppException exception = assertThrows(AppException.class, 
            () -> projectService.updateProgress(1L, 150));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testDelete_Success() {
        // Given
        when(projectRepo.existsById(1L)).thenReturn(true);
        doNothing().when(projectRepo).deleteById(1L);

        // When
        projectService.delete(1L);

        // Then
        verify(projectRepo, times(1)).existsById(1L);
        verify(projectRepo, times(1)).deleteById(1L);
    }

    @Test
    void testFindByStatus_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Project> page = new PageImpl<>(Arrays.asList(project));
        when(projectRepo.findByStatus(ProjectStatus.DRAFT, pageable)).thenReturn(page);
        when(documentRepo.countByProjectId(anyLong())).thenReturn(0L);

        // When
        Page<ProjectDto> result = projectService.findByStatus(ProjectStatus.DRAFT, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(projectRepo, times(1)).findByStatus(ProjectStatus.DRAFT, pageable);
    }

    @Test
    void testSearch_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Project> page = new PageImpl<>(Arrays.asList(project));
        when(projectRepo.search(anyString(), any(Pageable.class))).thenReturn(page);
        when(documentRepo.countByProjectId(anyLong())).thenReturn(0L);

        // When
        Page<ProjectDto> result = projectService.search("rénovation", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(projectRepo, times(1)).search("rénovation", pageable);
    }

    @Test
    void testCountByStatus() {
        // Given
        when(projectRepo.countByStatus(ProjectStatus.IN_PROGRESS)).thenReturn(5L);

        // When
        long count = projectService.countByStatus(ProjectStatus.IN_PROGRESS);

        // Then
        assertEquals(5L, count);
        verify(projectRepo, times(1)).countByStatus(ProjectStatus.IN_PROGRESS);
    }
}

