package com.gp_dev.erp_lite.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Document associé à un chantier (photos, plans, etc.)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "gp_erp_project_document")
public class ProjectDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Projet associé
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * Nom du fichier original
     */
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    /**
     * Nom du fichier stocké
     */
    @Column(name = "stored_filename", nullable = false, length = 255)
    private String storedFilename;

    /**
     * Type MIME du fichier
     */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /**
     * Taille du fichier en bytes
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * Type de document (PHOTO, PLAN, CONTRACT, OTHER)
     */
    @Column(name = "document_type", length = 50)
    @Builder.Default
    private String documentType = "OTHER";

    /**
     * Description du document
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Utilisateur qui a uploadé le document
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;
}

