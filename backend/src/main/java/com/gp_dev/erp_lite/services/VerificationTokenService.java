package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.models.TokenType;
import com.gp_dev.erp_lite.models.User;
import com.gp_dev.erp_lite.models.VerificationToken;

public interface VerificationTokenService {
    VerificationToken createToken(User user, TokenType type);
    VerificationToken verifyToken(String token, TokenType type);
    void deleteTokensByUser(User user);
}
