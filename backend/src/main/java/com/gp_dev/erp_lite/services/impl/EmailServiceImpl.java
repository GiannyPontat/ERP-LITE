package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.InvoiceDto;
import com.gp_dev.erp_lite.dtos.QuoteDto;
import com.gp_dev.erp_lite.services.EmailService;
import com.gp_dev.erp_lite.services.PdfService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final PdfService pdfService;

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

    @Override
    public void sendQuoteEmail(QuoteDto quoteDto, String recipientEmail) {
        try {
            String subject = "Devis " + quoteDto.getQuoteNumber() + " - ERP Lite";
            String htmlContent = getQuoteEmailTemplate(quoteDto);
            byte[] pdfBytes = pdfService.generateQuotePdf(quoteDto);

            sendEmailWithAttachment(recipientEmail, subject, htmlContent, pdfBytes,
                "devis-" + quoteDto.getQuoteNumber() + ".pdf");

            log.info("Quote email sent to: {} for quote: {}", recipientEmail, quoteDto.getQuoteNumber());
        } catch (Exception e) {
            log.error("Failed to send quote email to {}: {}", recipientEmail, e.getMessage());
            throw new RuntimeException("Failed to send quote email", e);
        }
    }

    @Override
    public void sendInvoiceEmail(InvoiceDto invoiceDto, String recipientEmail) {
        try {
            String subject = "Facture " + invoiceDto.getInvoiceNumber() + " - ERP Lite";
            String htmlContent = getInvoiceEmailTemplate(invoiceDto);
            byte[] pdfBytes = pdfService.generateInvoicePdf(invoiceDto);

            sendEmailWithAttachment(recipientEmail, subject, htmlContent, pdfBytes,
                "facture-" + invoiceDto.getInvoiceNumber() + ".pdf");

            log.info("Invoice email sent to: {} for invoice: {}", recipientEmail, invoiceDto.getInvoiceNumber());
        } catch (Exception e) {
            log.error("Failed to send invoice email to {}: {}", recipientEmail, e.getMessage());
            throw new RuntimeException("Failed to send invoice email", e);
        }
    }

    private void sendEmailWithAttachment(String to, String subject, String htmlContent,
                                        byte[] attachment, String attachmentName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Ajouter la pièce jointe PDF
            helper.addAttachment(attachmentName, new ByteArrayResource(attachment));

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email with attachment to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email with attachment", e);
        }
    }

    private String getQuoteEmailTemplate(QuoteDto quoteDto) {
        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #7C4DFF; color: white; padding: 20px; text-align: center; }
        .content { background-color: #f9f9f9; padding: 30px; }
        .info-box { background-color: #fff; border: 1px solid #ddd; border-radius: 4px;
                    padding: 15px; margin: 20px 0; }
        .info-row { display: flex; justify-content: space-between; padding: 8px 0;
                    border-bottom: 1px solid #eee; }
        .info-row:last-child { border-bottom: none; }
        .info-label { font-weight: bold; color: #666; }
        .info-value { color: #333; }
        .total { font-size: 1.2em; font-weight: bold; color: #7C4DFF; }
        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
        .attachment-notice { background-color: #e3f2fd; border-left: 4px solid #2196F3;
                             padding: 12px; margin: 20px 0; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Devis %s</h1>
        </div>
        <div class="content">
            <h2>Bonjour,</h2>
            <p>Veuillez trouver ci-joint votre devis.</p>

            <div class="info-box">
                <div class="info-row">
                    <span class="info-label">Numéro de devis:</span>
                    <span class="info-value">%s</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Date:</span>
                    <span class="info-value">%s</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Valable jusqu'au:</span>
                    <span class="info-value">%s</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Montant HT:</span>
                    <span class="info-value">%.2f €</span>
                </div>
                <div class="info-row">
                    <span class="info-label">TVA (%.2f%%):</span>
                    <span class="info-value">%.2f €</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Total TTC:</span>
                    <span class="total">%.2f €</span>
                </div>
            </div>

            <div class="attachment-notice">
                <p><strong>📎 Pièce jointe:</strong> Le devis complet est disponible en PDF joint à cet email.</p>
            </div>

            <p>Pour toute question concernant ce devis, n'hésitez pas à nous contacter.</p>
            <p>Cordialement,<br>L'équipe ERP Lite</p>
        </div>
        <div class="footer">
            <p>© 2026 ERP Lite. Tous droits réservés.</p>
        </div>
    </div>
</body>
</html>
""".formatted(
            quoteDto.getQuoteNumber(),
            quoteDto.getQuoteNumber(),
            quoteDto.getDate(),
            quoteDto.getValidUntil() != null ? quoteDto.getValidUntil() : "N/A",
            quoteDto.getSubtotal(),
            quoteDto.getTaxRate(),
            quoteDto.getTaxAmount(),
            quoteDto.getTotal()
        );
    }

    private String getInvoiceEmailTemplate(InvoiceDto invoiceDto) {
        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #F44336; color: white; padding: 20px; text-align: center; }
        .content { background-color: #f9f9f9; padding: 30px; }
        .info-box { background-color: #fff; border: 1px solid #ddd; border-radius: 4px;
                    padding: 15px; margin: 20px 0; }
        .info-row { display: flex; justify-content: space-between; padding: 8px 0;
                    border-bottom: 1px solid #eee; }
        .info-row:last-child { border-bottom: none; }
        .info-label { font-weight: bold; color: #666; }
        .info-value { color: #333; }
        .total { font-size: 1.2em; font-weight: bold; color: #F44336; }
        .status { display: inline-block; padding: 4px 12px; border-radius: 12px;
                  font-size: 0.9em; font-weight: bold; }
        .status-paid { background-color: #4CAF50; color: white; }
        .status-pending { background-color: #FF9800; color: white; }
        .status-overdue { background-color: #F44336; color: white; }
        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
        .attachment-notice { background-color: #e3f2fd; border-left: 4px solid #2196F3;
                             padding: 12px; margin: 20px 0; }
        .payment-info { background-color: #fff3cd; border-left: 4px solid #ffc107;
                        padding: 12px; margin: 20px 0; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Facture %s</h1>
        </div>
        <div class="content">
            <h2>Bonjour,</h2>
            <p>Veuillez trouver ci-joint votre facture.</p>

            <div class="info-box">
                <div class="info-row">
                    <span class="info-label">Numéro de facture:</span>
                    <span class="info-value">%s</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Date:</span>
                    <span class="info-value">%s</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Date d'échéance:</span>
                    <span class="info-value">%s</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Statut:</span>
                    <span class="info-value"><span class="status status-%s">%s</span></span>
                </div>
                <div class="info-row">
                    <span class="info-label">Montant HT:</span>
                    <span class="info-value">%.2f €</span>
                </div>
                <div class="info-row">
                    <span class="info-label">TVA (%.2f%%):</span>
                    <span class="info-value">%.2f €</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Total TTC:</span>
                    <span class="total">%.2f €</span>
                </div>
            </div>

            <div class="attachment-notice">
                <p><strong>📎 Pièce jointe:</strong> La facture complète est disponible en PDF joint à cet email.</p>
            </div>

            %s

            <p>Pour toute question concernant cette facture, n'hésitez pas à nous contacter.</p>
            <p>Cordialement,<br>L'équipe ERP Lite</p>
        </div>
        <div class="footer">
            <p>© 2026 ERP Lite. Tous droits réservés.</p>
        </div>
    </div>
</body>
</html>
""".formatted(
            invoiceDto.getInvoiceNumber(),
            invoiceDto.getInvoiceNumber(),
            invoiceDto.getDate(),
            invoiceDto.getDueDate(),
            invoiceDto.getStatus().toString().toLowerCase(),
            invoiceDto.getStatus(),
            invoiceDto.getSubtotal(),
            invoiceDto.getTaxRate(),
            invoiceDto.getTaxAmount(),
            invoiceDto.getTotal(),
            invoiceDto.getStatus().toString().equals("PAID") ? "" :
                "<div class=\"payment-info\"><p><strong>⚠️ Paiement requis:</strong> Cette facture est en attente de paiement. Merci de procéder au règlement avant la date d'échéance.</p></div>"
        );
    }
}
