package com.gp_dev.erp_lite.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class RefreshTokenDto {
    private Long id;
    private String token;
    private LocalDateTime expiryDate;
    private Boolean revoked;
}
