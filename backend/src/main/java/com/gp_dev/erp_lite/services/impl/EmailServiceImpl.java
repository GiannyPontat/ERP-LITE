package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.url}")
    private String appUrl;

    @Override
    public void sendVerificationEmail(String to, String token) {
        String subject = "Verify Your Email - ERP Lite";
        String verificationLink = appUrl + "/verify-email?token=" + token;
        String htmlContent = getVerificationEmailTemplate(verificationLink);

        sendHtmlEmail(to, subject, htmlContent);
        log.info("Verification email sent to: {}", to);
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Password Reset Request - ERP Lite";
        String resetLink = appUrl + "/reset-password?token=" + token;
        String htmlContent = getPasswordResetEmailTemplate(resetLink);

        sendHtmlEmail(to, subject, htmlContent);
        log.info("Password reset email sent to: {}", to);
    }

    @Override
    public void sendPasswordChangeConfirmation(String to) {
        String subject = "Password Changed - ERP Lite";
        String htmlContent = getPasswordChangeConfirmationTemplate();

        sendHtmlEmail(to, subject, htmlContent);
        log.info("Password change confirmation sent to: {}", to);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String getVerificationEmailTemplate(String verificationLink) {
        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
        .content { background-color: #f9f9f9; padding: 30px; }
        .button { display: inline-block; padding: 12px 24px; background-color: #4CAF50;
                  color: white; text-decoration: none; border-radius: 4px; margin: 20px 0; }
        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Welcome to ERP Lite!</h1>
        </div>
        <div class="content">
            <h2>Verify Your Email Address</h2>
            <p>Thank you for registering with ERP Lite. To complete your registration, please verify your email address by clicking the button below:</p>
            <a href="%s" class="button">Verify Email</a>
            <p>Or copy and paste this link into your browser:</p>
            <p style="word-break: break-all;">%s</p>
            <p><strong>This link will expire in 24 hours.</strong></p>
            <p>If you didn't create an account with ERP Lite, please ignore this email.</p>
        </div>
        <div class="footer">
            <p>© 2026 ERP Lite. All rights reserved.</p>
        </div>
    </div>
</body>
</html>
""".formatted(verificationLink, verificationLink);
    }

    private String getPasswordResetEmailTemplate(String resetLink) {
        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }
        .content { background-color: #f9f9f9; padding: 30px; }
        .button { display: inline-block; padding: 12px 24px; background-color: #2196F3;
                  color: white; text-decoration: none; border-radius: 4px; margin: 20px 0; }
        .warning { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 12px; margin: 20px 0; }
        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Password Reset Request</h1>
        </div>
        <div class="content">
            <h2>Reset Your Password</h2>
            <p>We received a request to reset your password for your ERP Lite account. Click the button below to reset it:</p>
            <a href="%s" class="button">Reset Password</a>
            <p>Or copy and paste this link into your browser:</p>
            <p style="word-break: break-all;">%s</p>
            <div class="warning">
                <p><strong>This link will expire in 15 minutes.</strong></p>
            </div>
            <p>If you didn't request a password reset, please ignore this email or contact support if you have concerns.</p>
        </div>
        <div class="footer">
            <p>© 2026 ERP Lite. All rights reserved.</p>
        </div>
    </div>
</body>
</html>
""".formatted(resetLink, resetLink);
    }

    private String getPasswordChangeConfirmationTemplate() {
        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
        .content { background-color: #f9f9f9; padding: 30px; }
        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Password Changed Successfully</h1>
        </div>
        <div class="content">
            <h2>Your Password Has Been Changed</h2>
            <p>This is a confirmation that the password for your ERP Lite account has been successfully changed.</p>
            <p>If you did not make this change, please contact support immediately.</p>
        </div>
        <div class="footer">
            <p>© 2026 ERP Lite. All rights reserved.</p>
        </div>
    </div>
</body>
</html>
""";
    }
}
