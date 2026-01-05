package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.ProjectDocumentDto;
import com.gp_dev.erp_lite.dtos.ProjectDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.*;
import com.gp_dev.erp_lite.repositories.*;
import com.gp_dev.erp_lite.services.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepo projectRepo;
    private final ProjectDocumentRepo documentRepo;
    private final ClientRepo clientRepo;
    private final UserRepo userRepo;

    @Value("${app.upload.dir:uploads/projects}")
    private String uploadDir;

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDto> findAll(Pageable pageable) {
        return projectRepo.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto findById(Long id) {
        Project project = projectRepo.findById(id)
                .orElseThrow(() -> new AppException("Project not found", HttpStatus.NOT_FOUND));
        return toDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto findByReference(String reference) {
        Project project = projectRepo.findByReference(reference)
                .orElseThrow(() -> new AppException("Project not found with reference: " + reference, HttpStatus.NOT_FOUND));
        return toDto(project);
    }

    @Override
    public ProjectDto create(ProjectDto dto) {
        Client client = clientRepo.findById(dto.getClientId())
                .orElseThrow(() -> new AppException("Client not found", HttpStatus.NOT_FOUND));

        User createdBy = userRepo.findById(dto.getCreatedById())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        User manager = null;
        if (dto.getManagerId() != null) {
            manager = userRepo.findById(dto.getManagerId())
                    .orElseThrow(() -> new AppException("Manager not found", HttpStatus.NOT_FOUND));
        }

        String reference = generateReference();

        Project project = Project.builder()
                .reference(reference)
                .name(dto.getName())
                .description(dto.getDescription())
                .client(client)
                .createdBy(createdBy)
                .manager(manager)
                .status(dto.getStatus() != null ? dto.getStatus() : ProjectStatus.DRAFT)
                .siteAddress(dto.getSiteAddress())
                .siteCity(dto.getSiteCity())
                .sitePostalCode(dto.getSitePostalCode())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .estimatedBudget(dto.getEstimatedBudget())
                .progressPercentage(dto.getProgressPercentage() != null ? dto.getProgressPercentage() : 0)
                .notes(dto.getNotes())
                .build();

        project = projectRepo.save(project);
        log.info("Created project: {}", project.getReference());
        return toDto(project);
    }

    @Override
    public ProjectDto update(Long id, ProjectDto dto) {
        Project project = projectRepo.findById(id)
                .orElseThrow(() -> new AppException("Project not found", HttpStatus.NOT_FOUND));

        if (dto.getName() != null) project.setName(dto.getName());
        if (dto.getDescription() != null) project.setDescription(dto.getDescription());
        
        if (dto.getClientId() != null && !dto.getClientId().equals(project.getClient().getId())) {
            Client client = clientRepo.findById(dto.getClientId())
                    .orElseThrow(() -> new AppException("Client not found", HttpStatus.NOT_FOUND));
            project.setClient(client);
        }

        if (dto.getManagerId() != null) {
            User manager = userRepo.findById(dto.getManagerId())
                    .orElseThrow(() -> new AppException("Manager not found", HttpStatus.NOT_FOUND));
            project.setManager(manager);
        }

        if (dto.getStatus() != null) project.setStatus(dto.getStatus());
        if (dto.getSiteAddress() != null) project.setSiteAddress(dto.getSiteAddress());
        if (dto.getSiteCity() != null) project.setSiteCity(dto.getSiteCity());
        if (dto.getSitePostalCode() != null) project.setSitePostalCode(dto.getSitePostalCode());
        if (dto.getStartDate() != null) project.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) project.setEndDate(dto.getEndDate());
        if (dto.getActualStartDate() != null) project.setActualStartDate(dto.getActualStartDate());
        if (dto.getActualEndDate() != null) project.setActualEndDate(dto.getActualEndDate());
        if (dto.getEstimatedBudget() != null) project.setEstimatedBudget(dto.getEstimatedBudget());
        if (dto.getActualCost() != null) project.setActualCost(dto.getActualCost());
        if (dto.getProgressPercentage() != null) project.setProgressPercentage(dto.getProgressPercentage());
        if (dto.getNotes() != null) project.setNotes(dto.getNotes());

        project = projectRepo.save(project);
        log.info("Updated project: {}", project.getReference());
        return toDto(project);
    }

    @Override
    public void delete(Long id) {
        if (!projectRepo.existsById(id)) {
            throw new AppException("Project not found", HttpStatus.NOT_FOUND);
        }
        projectRepo.deleteById(id);
        log.info("Deleted project with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDto> findByClientId(Long clientId, Pageable pageable) {
        return projectRepo.findByClientId(clientId, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findByManagerId(Long managerId) {
        return projectRepo.findByManagerId(managerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDto> findByStatus(ProjectStatus status, Pageable pageable) {
        return projectRepo.findByStatus(status, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDto> search(String search, Pageable pageable) {
        return projectRepo.search(search, pageable).map(this::toDto);
    }

    @Override
    public ProjectDto updateStatus(Long id, ProjectStatus status) {
        Project project = projectRepo.findById(id)
                .orElseThrow(() -> new AppException("Project not found", HttpStatus.NOT_FOUND));
        
        project.setStatus(status);
        
        // Mettre à jour les dates réelles selon le statut
        if (status == ProjectStatus.IN_PROGRESS && project.getActualStartDate() == null) {
            project.setActualStartDate(LocalDate.now());
        } else if (status == ProjectStatus.COMPLETED && project.getActualEndDate() == null) {
            project.setActualEndDate(LocalDate.now());
            project.setProgressPercentage(100);
        }
        
        project = projectRepo.save(project);
        log.info("Updated project {} status to {}", project.getReference(), status);
        return toDto(project);
    }

    @Override
    public ProjectDto updateProgress(Long id, Integer progressPercentage) {
        Project project = projectRepo.findById(id)
                .orElseThrow(() -> new AppException("Project not found", HttpStatus.NOT_FOUND));
        
        if (progressPercentage < 0 || progressPercentage > 100) {
            throw new AppException("Progress must be between 0 and 100", HttpStatus.BAD_REQUEST);
        }
        
        project.setProgressPercentage(progressPercentage);
        
        if (progressPercentage == 100 && project.getStatus() != ProjectStatus.COMPLETED) {
            project.setStatus(ProjectStatus.COMPLETED);
            project.setActualEndDate(LocalDate.now());
        }
        
        project = projectRepo.save(project);
        log.info("Updated project {} progress to {}%", project.getReference(), progressPercentage);
        return toDto(project);
    }

    @Override
    public ProjectDocumentDto uploadDocument(Long projectId, MultipartFile file, String documentType, String description) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new AppException("Project not found", HttpStatus.NOT_FOUND));

        try {
            // Créer le répertoire si nécessaire
            Path uploadPath = Paths.get(uploadDir, projectId.toString());
            Files.createDirectories(uploadPath);

            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(storedFilename);

            // Sauvegarder le fichier
            Files.copy(file.getInputStream(), filePath);

            // Créer l'entrée en base
            ProjectDocument document = ProjectDocument.builder()
                    .project(project)
                    .originalFilename(originalFilename)
                    .storedFilename(storedFilename)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .documentType(documentType != null ? documentType : "OTHER")
                    .description(description)
                    .build();

            document = documentRepo.save(document);
            log.info("Uploaded document {} for project {}", originalFilename, project.getReference());
            return toDocumentDto(document);

        } catch (IOException e) {
            log.error("Failed to upload document", e);
            throw new AppException("Failed to upload document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDocumentDto> getDocuments(Long projectId) {
        return documentRepo.findByProjectId(projectId).stream()
                .map(this::toDocumentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDocument(Long documentId) {
        ProjectDocument document = documentRepo.findById(documentId)
                .orElseThrow(() -> new AppException("Document not found", HttpStatus.NOT_FOUND));

        try {
            // Supprimer le fichier physique
            Path filePath = Paths.get(uploadDir, document.getProject().getId().toString(), document.getStoredFilename());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", e.getMessage());
        }

        documentRepo.delete(document);
        log.info("Deleted document {}", document.getOriginalFilename());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadDocument(Long documentId) {
        ProjectDocument document = documentRepo.findById(documentId)
                .orElseThrow(() -> new AppException("Document not found", HttpStatus.NOT_FOUND));

        try {
            Path filePath = Paths.get(uploadDir, document.getProject().getId().toString(), document.getStoredFilename());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read document file", e);
            throw new AppException("Failed to read document", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findOverdueProjects() {
        return projectRepo.findOverdueProjects(LocalDate.now()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findUpcomingProjects(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return projectRepo.findUpcomingProjects(today, futureDate).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(ProjectStatus status) {
        return projectRepo.countByStatus(status);
    }

    private String generateReference() {
        String prefix = "CHANT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy")) + "-";
        String lastReference = projectRepo.findLastReferenceByPrefix(prefix + "%")
                .orElse(prefix + "0000");
        int lastNumber = Integer.parseInt(lastReference.substring(lastReference.length() - 4));
        return prefix + String.format("%04d", lastNumber + 1);
    }

    private ProjectDto toDto(Project project) {
        ProjectDto dto = ProjectDto.builder()
                .id(project.getId())
                .reference(project.getReference())
                .name(project.getName())
                .description(project.getDescription())
                .clientId(project.getClient().getId())
                .clientName(project.getClient().getCompanyName() != null ? 
                        project.getClient().getCompanyName() : project.getClient().getNom())
                .managerId(project.getManager() != null ? project.getManager().getId() : null)
                .managerName(project.getManager() != null ? 
                        project.getManager().getFirstName() + " " + project.getManager().getLastName() : null)
                .createdById(project.getCreatedBy().getId())
                .createdByName(project.getCreatedBy().getFirstName() + " " + project.getCreatedBy().getLastName())
                .status(project.getStatus())
                .statusDisplayName(project.getStatus() != null ? project.getStatus().getDisplayName() : null)
                .siteAddress(project.getSiteAddress())
                .siteCity(project.getSiteCity())
                .sitePostalCode(project.getSitePostalCode())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .actualStartDate(project.getActualStartDate())
                .actualEndDate(project.getActualEndDate())
                .estimatedBudget(project.getEstimatedBudget())
                .actualCost(project.getActualCost())
                .progressPercentage(project.getProgressPercentage())
                .notes(project.getNotes())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();

        // Compteurs
        dto.setQuotesCount(project.getQuotes() != null ? project.getQuotes().size() : 0);
        dto.setInvoicesCount(project.getInvoices() != null ? project.getInvoices().size() : 0);
        dto.setDocumentsCount((int) documentRepo.countByProjectId(project.getId()));

        // Calculer les totaux
        if (project.getQuotes() != null) {
            dto.setTotalQuotesAmount(project.getQuotes().stream()
                    .map(Quote::getTotal)
                    .filter(t -> t != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        if (project.getInvoices() != null) {
            dto.setTotalInvoicesAmount(project.getInvoices().stream()
                    .map(Invoice::getTotal)
                    .filter(t -> t != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            BigDecimal paidAmount = project.getInvoices().stream()
                    .filter(i -> i.getStatus() == InvoiceStatus.PAID)
                    .map(Invoice::getTotal)
                    .filter(t -> t != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setTotalPaidAmount(paidAmount);
            dto.setRemainingToPay(dto.getTotalInvoicesAmount().subtract(paidAmount));
        }

        return dto;
    }

    private ProjectDocumentDto toDocumentDto(ProjectDocument doc) {
        return ProjectDocumentDto.builder()
                .id(doc.getId())
                .projectId(doc.getProject().getId())
                .originalFilename(doc.getOriginalFilename())
                .storedFilename(doc.getStoredFilename())
                .contentType(doc.getContentType())
                .fileSize(doc.getFileSize())
                .documentType(doc.getDocumentType())
                .description(doc.getDescription())
                .uploadedById(doc.getUploadedBy() != null ? doc.getUploadedBy().getId() : null)
                .uploadedByName(doc.getUploadedBy() != null ? 
                        doc.getUploadedBy().getFirstName() + " " + doc.getUploadedBy().getLastName() : null)
                .uploadedAt(doc.getUploadedAt())
                .downloadUrl("/api/v1/projects/" + doc.getProject().getId() + "/documents/" + doc.getId() + "/download")
                .build();
    }
}

