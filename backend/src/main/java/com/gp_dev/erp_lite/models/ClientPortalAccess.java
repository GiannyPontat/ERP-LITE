package com.gp_dev.erp_lite.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Accès au portail client
 * Permet aux clients de se connecter pour consulter leurs devis/factures
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "gp_erp_client_portal_access", indexes = {
    @Index(name = "idx_portal_email", columnList = "email"),
    @Index(name = "idx_portal_client", columnList = "client_id")
})
public class ClientPortalAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Client associé
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * Email de connexion (peut être différent de l'email du client)
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Mot de passe hashé
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Compte actif ou non
     */
    @Builder.Default
    private Boolean active = true;

    /**
     * Email vérifié
     */
    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    /**
     * Token de vérification email
     */
    @Column(name = "verification_token", length = 255)
    private String verificationToken;

    /**
     * Token de reset password
     */
    @Column(name = "reset_token", length = 255)
    private String resetToken;

    /**
     * Expiration du token de reset
     */
    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    /**
     * Dernière connexion
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

