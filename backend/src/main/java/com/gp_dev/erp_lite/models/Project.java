package com.gp_dev.erp_lite.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un chantier/projet
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "gp_erp_project", indexes = {
    @Index(name = "idx_project_reference", columnList = "reference"),
    @Index(name = "idx_project_client", columnList = "client_id"),
    @Index(name = "idx_project_status", columnList = "status")
})
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Référence unique du chantier (ex: "CHANT-2026-0001")
     */
    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    /**
     * Nom du chantier
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * Description du chantier
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Client associé au chantier
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * Responsable du chantier
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    /**
     * Créateur du chantier
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    /**
     * Statut du chantier
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.DRAFT;

    // === Adresse du chantier ===
    @Column(name = "site_address", length = 500)
    private String siteAddress;

    @Column(name = "site_city", length = 100)
    private String siteCity;

    @Column(name = "site_postal_code", length = 20)
    private String sitePostalCode;

    // === Dates ===
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "actual_start_date")
    private LocalDate actualStartDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    // === Budget ===
    @Column(name = "estimated_budget", precision = 12, scale = 2)
    private BigDecimal estimatedBudget;

    @Column(name = "actual_cost", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal actualCost = BigDecimal.ZERO;

    // === Progression ===
    @Column(name = "progress_percentage")
    @Builder.Default
    private Integer progressPercentage = 0;

    // === Notes ===
    @Column(columnDefinition = "TEXT")
    private String notes;

    // === Relations avec devis et factures ===
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Quote> quotes = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Invoice> invoices = new ArrayList<>();

    // === Documents ===
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectDocument> documents = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

