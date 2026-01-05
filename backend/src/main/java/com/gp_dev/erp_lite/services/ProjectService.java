package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.ProjectDocumentDto;
import com.gp_dev.erp_lite.dtos.ProjectDto;
import com.gp_dev.erp_lite.models.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {

    Page<ProjectDto> findAll(Pageable pageable);

    ProjectDto findById(Long id);

    ProjectDto findByReference(String reference);

    ProjectDto create(ProjectDto projectDto);

    ProjectDto update(Long id, ProjectDto projectDto);

    void delete(Long id);

    Page<ProjectDto> findByClientId(Long clientId, Pageable pageable);

    List<ProjectDto> findByManagerId(Long managerId);

    Page<ProjectDto> findByStatus(ProjectStatus status, Pageable pageable);

    Page<ProjectDto> search(String search, Pageable pageable);

    ProjectDto updateStatus(Long id, ProjectStatus status);

    ProjectDto updateProgress(Long id, Integer progressPercentage);

    // Gestion des documents
    ProjectDocumentDto uploadDocument(Long projectId, MultipartFile file, String documentType, String description);

    List<ProjectDocumentDto> getDocuments(Long projectId);

    void deleteDocument(Long documentId);

    byte[] downloadDocument(Long documentId);

    // Statistiques
    List<ProjectDto> findOverdueProjects();

    List<ProjectDto> findUpcomingProjects(int days);

    long countByStatus(ProjectStatus status);
}

