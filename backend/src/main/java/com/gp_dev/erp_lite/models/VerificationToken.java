package com.gp_dev.erp_lite.models;

import com.gp_dev.erp_lite.dtos.VerificationTokenDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "gp_erp_verification_token")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean used = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType type;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public VerificationTokenDto dto() {
        return new VerificationTokenDto(
                id,
                token,
                expiryDate,
                used,
                type
        );
    }
}
