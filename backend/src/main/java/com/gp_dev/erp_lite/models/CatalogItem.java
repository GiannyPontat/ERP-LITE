package com.gp_dev.erp_lite.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Article du catalogue de prix BTP
 * Permet de créer une bibliothèque de prix pour faciliter la création de devis
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "gp_erp_catalog_item", indexes = {
    @Index(name = "idx_catalog_reference", columnList = "reference"),
    @Index(name = "idx_catalog_category", columnList = "category"),
    @Index(name = "idx_catalog_designation", columnList = "designation")
})
public class CatalogItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Référence unique de l'article (ex: "ELEC-001", "PLOMB-042")
     */
    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    /**
     * Désignation / nom de l'article
     */
    @Column(nullable = false, length = 500)
    private String designation;

    /**
     * Description détaillée de l'article
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Catégorie de l'article
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CatalogCategory category;

    /**
     * Unité de mesure (ex: "m²", "ml", "u", "h", "forfait")
     */
    @Column(nullable = false, length = 20)
    private String unit;

    /**
     * Prix unitaire HT
     */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Taux de TVA applicable (ex: 20.00, 10.00, 5.50)
     */
    @Column(name = "tax_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = new BigDecimal("20.00");

    /**
     * Coût d'achat (pour calcul de marge)
     */
    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice;

    /**
     * Fournisseur principal
     */
    @Column(length = 255)
    private String supplier;

    /**
     * Marque du produit
     */
    @Column(length = 100)
    private String brand;

    /**
     * Référence fabricant
     */
    @Column(name = "manufacturer_reference", length = 100)
    private String manufacturerReference;

    /**
     * Article actif ou non
     */
    @Builder.Default
    private Boolean active = true;

    /**
     * Notes internes
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Utilisateur propriétaire (pour filtrer par entreprise)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

