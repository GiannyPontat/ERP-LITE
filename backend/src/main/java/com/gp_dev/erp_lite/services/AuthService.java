package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.*;

public interface AuthService {
    MessageResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String email);
    MessageResponse verifyEmail(String token);
    MessageResponse forgotPassword(String email);
    MessageResponse resetPassword(ResetPasswordRequest request);
    MessageResponse resendVerificationEmail(String email);
}
