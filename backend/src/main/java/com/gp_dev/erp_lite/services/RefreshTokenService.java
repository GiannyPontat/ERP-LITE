package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.models.RefreshToken;
import com.gp_dev.erp_lite.models.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String email);
    RefreshToken verifyExpiration(RefreshToken token);
    void revokeTokensByUser(User user);
}
