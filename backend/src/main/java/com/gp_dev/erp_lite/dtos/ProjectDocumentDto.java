package com.gp_dev.erp_lite.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDocumentDto {

    private Long id;
    private Long projectId;
    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private Long fileSize;
    private String documentType;
    private String description;
    private Long uploadedById;
    private String uploadedByName;
    private LocalDateTime uploadedAt;
    private String downloadUrl;
}

