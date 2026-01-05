package com.gp_dev.erp_lite.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientPortalAuthResponse {

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private Long clientId;
    private String clientName;
    private String email;
}

