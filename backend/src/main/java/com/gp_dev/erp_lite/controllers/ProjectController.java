package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.ErrorResponse;
import com.gp_dev.erp_lite.dtos.ProjectDocumentDto;
import com.gp_dev.erp_lite.dtos.ProjectDto;
import com.gp_dev.erp_lite.models.ProjectStatus;
import com.gp_dev.erp_lite.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Projects", description = "Project/Site management endpoints")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Get all projects", description = "Retrieves paginated list of all projects",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Page<ProjectDto>> getAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(projectService.findAll(pageable));
    }

    @Operation(summary = "Get project by ID", description = "Retrieves detailed information about a specific project",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Project found",
            content = @Content(schema = @Schema(implementation = ProjectDto.class))),
        @ApiResponse(responseCode = "404", description = "Project not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<ProjectDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @Operation(summary = "Get project by reference", description = "Retrieves a project by its reference code",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<ProjectDto> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(projectService.findByReference(reference));
    }

    @Operation(summary = "Create project", description = "Creates a new project/site",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProjectDto> create(@Valid @RequestBody ProjectDto projectDto) {
        ProjectDto created = projectService.create(projectDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update project", description = "Updates an existing project",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProjectDto> update(@PathVariable Long id, @Valid @RequestBody ProjectDto projectDto) {
        return ResponseEntity.ok(projectService.update(id, projectDto));
    }

    @Operation(summary = "Delete project", description = "Deletes a project and all associated data",
        security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get projects by client", description = "Retrieves all projects for a specific client",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Page<ProjectDto>> getByClientId(
            @PathVariable Long clientId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(projectService.findByClientId(clientId, pageable));
    }

    @Operation(summary = "Get projects by manager", description = "Retrieves all projects managed by a specific user",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/manager/{managerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<ProjectDto>> getByManagerId(@PathVariable Long managerId) {
        return ResponseEntity.ok(projectService.findByManagerId(managerId));
    }

    @Operation(summary = "Get projects by status", description = "Retrieves all projects with a specific status",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Page<ProjectDto>> getByStatus(
            @PathVariable ProjectStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(projectService.findByStatus(status, pageable));
    }

    @Operation(summary = "Search projects", description = "Search projects by reference, name or description",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Page<ProjectDto>> search(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(projectService.search(query, pageable));
    }

    @Operation(summary = "Update project status", description = "Updates the status of a project",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProjectDto> updateStatus(
            @PathVariable Long id,
            @RequestParam ProjectStatus status) {
        return ResponseEntity.ok(projectService.updateStatus(id, status));
    }

    @Operation(summary = "Update project progress", description = "Updates the progress percentage of a project",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProjectDto> updateProgress(
            @PathVariable Long id,
            @RequestParam Integer progressPercentage) {
        return ResponseEntity.ok(projectService.updateProgress(id, progressPercentage));
    }

    // === Document endpoints ===

    @Operation(summary = "Upload document", description = "Uploads a document to a project",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{id}/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<ProjectDocumentDto> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) String description) {
        ProjectDocumentDto document = projectService.uploadDocument(id, file, documentType, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @Operation(summary = "Get project documents", description = "Retrieves all documents for a project",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<ProjectDocumentDto>> getDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getDocuments(id));
    }

    @Operation(summary = "Download document", description = "Downloads a document from a project",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{projectId}/documents/{documentId}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long projectId,
            @PathVariable Long documentId) {
        byte[] content = projectService.downloadDocument(documentId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(content.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }

    @Operation(summary = "Delete document", description = "Deletes a document from a project",
        security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{projectId}/documents/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long projectId,
            @PathVariable Long documentId) {
        projectService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }

    // === Statistics endpoints ===

    @Operation(summary = "Get overdue projects", description = "Retrieves projects that are past their end date",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<ProjectDto>> getOverdueProjects() {
        return ResponseEntity.ok(projectService.findOverdueProjects());
    }

    @Operation(summary = "Get upcoming projects", description = "Retrieves projects starting within specified days",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<ProjectDto>> getUpcomingProjects(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(projectService.findUpcomingProjects(days));
    }

    @Operation(summary = "Get project statistics", description = "Retrieves project count by status",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProjectStatsResponse> getStats() {
        return ResponseEntity.ok(new ProjectStatsResponse(
                projectService.countByStatus(ProjectStatus.DRAFT),
                projectService.countByStatus(ProjectStatus.PLANNING),
                projectService.countByStatus(ProjectStatus.IN_PROGRESS),
                projectService.countByStatus(ProjectStatus.ON_HOLD),
                projectService.countByStatus(ProjectStatus.COMPLETED),
                projectService.countByStatus(ProjectStatus.CANCELLED)
        ));
    }

    public record ProjectStatsResponse(
            long draft,
            long planning,
            long inProgress,
            long onHold,
            long completed,
            long cancelled
    ) {}
}

