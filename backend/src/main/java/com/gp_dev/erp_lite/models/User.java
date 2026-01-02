package com.gp_dev.erp_lite.models;

import com.gp_dev.erp_lite.dtos.UserDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "gp_erp_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Gardé en Long pour compatibilité avec code existant
    
    @Column(unique = true, updatable = false, nullable = false)
    private UUID uuid; // Identifiant UUID selon spec

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    // Nouveau champ selon spec
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole role;

    // Nouveau champ selon spec
    private String company;

    // Nouveau champ selon spec - remplace enabled
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    // Champs pour compatibilité avec code existant
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(nullable = false, name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    // Relation ManyToMany pour compatibilité avec code existant
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "gp_erp_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // Nouveaux champs selon spec
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Synchroniser active et enabled
        if (enabled != null) {
            active = enabled;
        }
    }

    // Méthode pour compatibilité
    public UserDto dto() {
        return new UserDto(
                id,
                email,
                firstName,
                lastName,
                enabled != null ? enabled : active,
                emailVerified,
                roles.stream().map(Role::dto).collect(Collectors.toList())
        );
    }
    
    @PrePersist
    protected void generateUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
    }
}
