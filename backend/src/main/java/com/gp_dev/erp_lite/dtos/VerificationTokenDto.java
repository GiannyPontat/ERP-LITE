package com.gp_dev.erp_lite.dtos;

import com.gp_dev.erp_lite.models.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class VerificationTokenDto {
    private Long id;
    private String token;
    private LocalDateTime expiryDate;
    private Boolean used;
    private TokenType type;
}
