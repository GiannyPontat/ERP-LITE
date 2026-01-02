package com.gp_dev.erp_lite.services;

public interface EmailService {
    void sendVerificationEmail(String to, String token);
    void sendPasswordResetEmail(String to, String token);
    void sendPasswordChangeConfirmation(String to);
}
