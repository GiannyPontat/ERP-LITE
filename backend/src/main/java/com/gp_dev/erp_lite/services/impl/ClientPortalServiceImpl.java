package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.*;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.*;
import com.gp_dev.erp_lite.repositories.*;
import com.gp_dev.erp_lite.security.JwtUtil;
import com.gp_dev.erp_lite.services.ClientPortalService;
import com.gp_dev.erp_lite.services.EmailService;
import com.gp_dev.erp_lite.services.PdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class ClientPortalServiceImpl implements ClientPortalService {

    private final ClientPortalAccessRepo portalAccessRepo;
    private final ClientRepo clientRepo;
    private final QuoteRepo quoteRepo;
    private final InvoiceRepo invoiceRepo;
    private final ProjectRepo projectRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PdfService pdfService;
    private final EmailService emailService;

    @Override
    public ClientPortalAuthResponse login(ClientPortalLoginRequest request) {
        ClientPortalAccess access = portalAccessRepo.findByEmailAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!access.getEmailVerified()) {
            throw new AppException("Email not verified. Please check your email.", HttpStatus.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(request.getPassword(), access.getPassword())) {
            throw new AppException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        // Mettre à jour la dernière connexion
        access.setLastLogin(LocalDateTime.now());
        portalAccessRepo.save(access);

        // Générer le token JWT
        String token = jwtUtil.generateClientPortalToken(access.getEmail(), access.getClient().getId());

        Client client = access.getClient();
        String clientName = client.getCompanyName() != null ? client.getCompanyName() : client.getNom();

        log.info("Client portal login successful for: {}", request.getEmail());

        return ClientPortalAuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(86400L) // 24 heures
                .clientId(client.getId())
                .clientName(clientName)
                .email(access.getEmail())
                .build();
    }

    @Override
    public void createPortalAccess(ClientPortalRegisterRequest request) {
        Client client = clientRepo.findById(request.getClientId())
                .orElseThrow(() -> new AppException("Client not found", HttpStatus.NOT_FOUND));

        if (portalAccessRepo.existsByEmail(request.getEmail())) {
            throw new AppException("Email already registered", HttpStatus.CONFLICT);
        }

        String verificationToken = UUID.randomUUID().toString();

        ClientPortalAccess access = ClientPortalAccess.builder()
                .client(client)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .emailVerified(false)
                .verificationToken(verificationToken)
                .build();

        portalAccessRepo.save(access);

        // Envoyer l'email de vérification
        emailService.sendVerificationEmail(request.getEmail(), verificationToken);

        log.info("Created portal access for client: {} with email: {}", client.getId(), request.getEmail());
    }

    @Override
    public void verifyEmail(String token) {
        ClientPortalAccess access = portalAccessRepo.findByVerificationToken(token)
                .orElseThrow(() -> new AppException("Invalid verification token", HttpStatus.BAD_REQUEST));

        access.setEmailVerified(true);
        access.setVerificationToken(null);
        portalAccessRepo.save(access);

        log.info("Email verified for portal access: {}", access.getEmail());
    }

    @Override
    public void requestPasswordReset(String email) {
        ClientPortalAccess access = portalAccessRepo.findByEmail(email)
                .orElseThrow(() -> new AppException("Email not found", HttpStatus.NOT_FOUND));

        String resetToken = UUID.randomUUID().toString();
        access.setResetToken(resetToken);
        access.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
        portalAccessRepo.save(access);

        emailService.sendPasswordResetEmail(email, resetToken);

        log.info("Password reset requested for: {}", email);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        ClientPortalAccess access = portalAccessRepo.findByResetToken(token)
                .orElseThrow(() -> new AppException("Invalid reset token", HttpStatus.BAD_REQUEST));

        if (access.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new AppException("Reset token expired", HttpStatus.BAD_REQUEST);
        }

        access.setPassword(passwordEncoder.encode(newPassword));
        access.setResetToken(null);
        access.setResetTokenExpiry(null);
        portalAccessRepo.save(access);

        log.info("Password reset successful for: {}", access.getEmail());
    }

    @Override
    public void changePassword(Long portalAccessId, String oldPassword, String newPassword) {
        ClientPortalAccess access = portalAccessRepo.findById(portalAccessId)
                .orElseThrow(() -> new AppException("Portal access not found", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(oldPassword, access.getPassword())) {
            throw new AppException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }

        access.setPassword(passwordEncoder.encode(newPassword));
        portalAccessRepo.save(access);

        log.info("Password changed for: {}", access.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientPortalQuoteDto> getClientQuotes(Long clientId) {
        return quoteRepo.findByClientId(clientId).stream()
                .map(this::toPortalQuoteDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientPortalQuoteDto getQuote(Long clientId, Long quoteId) {
        Quote quote = quoteRepo.findById(quoteId)
                .orElseThrow(() -> new AppException("Quote not found", HttpStatus.NOT_FOUND));

        if (!quote.getClient().getId().equals(clientId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        return toPortalQuoteDto(quote);
    }

    @Override
    public ClientPortalQuoteDto acceptQuote(Long clientId, Long quoteId) {
        Quote quote = quoteRepo.findById(quoteId)
                .orElseThrow(() -> new AppException("Quote not found", HttpStatus.NOT_FOUND));

        if (!quote.getClient().getId().equals(clientId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        if (quote.getStatus() != QuoteStatus.SENT) {
            throw new AppException("Quote cannot be accepted in current status", HttpStatus.BAD_REQUEST);
        }

        quote.setStatus(QuoteStatus.ACCEPTED);
        quote = quoteRepo.save(quote);

        log.info("Quote {} accepted by client {}", quote.getQuoteNumber(), clientId);
        return toPortalQuoteDto(quote);
    }

    @Override
    public ClientPortalQuoteDto rejectQuote(Long clientId, Long quoteId) {
        Quote quote = quoteRepo.findById(quoteId)
                .orElseThrow(() -> new AppException("Quote not found", HttpStatus.NOT_FOUND));

        if (!quote.getClient().getId().equals(clientId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        if (quote.getStatus() != QuoteStatus.SENT) {
            throw new AppException("Quote cannot be rejected in current status", HttpStatus.BAD_REQUEST);
        }

        quote.setStatus(QuoteStatus.REJECTED);
        quote = quoteRepo.save(quote);

        log.info("Quote {} rejected by client {}", quote.getQuoteNumber(), clientId);
        return toPortalQuoteDto(quote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientPortalInvoiceDto> getClientInvoices(Long clientId) {
        return invoiceRepo.findByClientId(clientId).stream()
                .map(this::toPortalInvoiceDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientPortalInvoiceDto getInvoice(Long clientId, Long invoiceId) {
        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new AppException("Invoice not found", HttpStatus.NOT_FOUND));

        if (!invoice.getClient().getId().equals(clientId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        return toPortalInvoiceDto(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadQuotePdf(Long clientId, Long quoteId) {
        Quote quote = quoteRepo.findById(quoteId)
                .orElseThrow(() -> new AppException("Quote not found", HttpStatus.NOT_FOUND));

        if (!quote.getClient().getId().equals(clientId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        return pdfService.generateQuotePdf(toQuoteDto(quote));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadInvoicePdf(Long clientId, Long invoiceId) {
        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new AppException("Invoice not found", HttpStatus.NOT_FOUND));

        if (!invoice.getClient().getId().equals(clientId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        return pdfService.generateInvoicePdf(toInvoiceDto(invoice));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getClientProjects(Long clientId) {
        return projectRepo.findByClientId(clientId).stream()
                .map(this::toProjectDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto getProject(Long clientId, Long projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new AppException("Project not found", HttpStatus.NOT_FOUND));

        if (!project.getClient().getId().equals(clientId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        return toProjectDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPortalAccess(Long clientId) {
        List<ClientPortalAccess> accesses = portalAccessRepo.findByClientId(clientId);
        return accesses.stream().anyMatch(a -> a.getActive() && a.getEmailVerified());
    }

    @Override
    public void disablePortalAccess(Long clientId) {
        List<ClientPortalAccess> accesses = portalAccessRepo.findByClientId(clientId);
        for (ClientPortalAccess access : accesses) {
            access.setActive(false);
            portalAccessRepo.save(access);
        }
        log.info("Disabled portal access for client: {}", clientId);
    }

    @Override
    public void enablePortalAccess(Long clientId) {
        List<ClientPortalAccess> accesses = portalAccessRepo.findByClientId(clientId);
        for (ClientPortalAccess access : accesses) {
            access.setActive(true);
            portalAccessRepo.save(access);
        }
        log.info("Enabled portal access for client: {}", clientId);
    }

    // === Helper methods ===

    private ClientPortalQuoteDto toPortalQuoteDto(Quote quote) {
        boolean canAccept = quote.getStatus() == QuoteStatus.SENT;
        boolean canReject = quote.getStatus() == QuoteStatus.SENT;

        return ClientPortalQuoteDto.builder()
                .id(quote.getId())
                .quoteNumber(quote.getQuoteNumber())
                .date(quote.getDate())
                .validUntil(quote.getValidUntil())
                .status(quote.getStatus())
                .statusDisplayName(getQuoteStatusDisplayName(quote.getStatus()))
                .subtotal(quote.getSubtotal())
                .taxRate(quote.getTaxRate())
                .taxAmount(quote.getTaxAmount())
                .total(quote.getTotal())
                .notes(quote.getNotes())
                .items(quote.getItems() != null ? quote.getItems().stream()
                        .map(this::toQuoteItemDto)
                        .collect(Collectors.toList()) : null)
                .projectName(quote.getProject() != null ? quote.getProject().getName() : null)
                .canAccept(canAccept)
                .canReject(canReject)
                .build();
    }

    private ClientPortalInvoiceDto toPortalInvoiceDto(Invoice invoice) {
        LocalDate today = LocalDate.now();
        boolean isOverdue = invoice.getDueDate() != null && 
                invoice.getDueDate().isBefore(today) && 
                invoice.getStatus() != InvoiceStatus.PAID;
        
        Long daysUntilDue = invoice.getDueDate() != null ? 
                ChronoUnit.DAYS.between(today, invoice.getDueDate()) : null;

        return ClientPortalInvoiceDto.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .date(invoice.getDate())
                .dueDate(invoice.getDueDate())
                .paidDate(invoice.getPaidDate())
                .status(invoice.getStatus())
                .statusDisplayName(getInvoiceStatusDisplayName(invoice.getStatus()))
                .subtotal(invoice.getSubtotal())
                .taxRate(invoice.getTaxRate())
                .taxAmount(invoice.getTaxAmount())
                .total(invoice.getTotal())
                .notes(invoice.getNotes())
                .items(invoice.getItems() != null ? invoice.getItems().stream()
                        .map(this::toInvoiceItemDto)
                        .collect(Collectors.toList()) : null)
                .projectName(invoice.getProject() != null ? invoice.getProject().getName() : null)
                .quoteNumber(invoice.getQuote() != null ? invoice.getQuote().getQuoteNumber() : null)
                .isOverdue(isOverdue)
                .daysUntilDue(daysUntilDue)
                .build();
    }

    private QuoteItemDto toQuoteItemDto(QuoteItem item) {
        return QuoteItemDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .total(item.getTotal())
                .build();
    }

    private InvoiceItemDto toInvoiceItemDto(InvoiceItem item) {
        return InvoiceItemDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .total(item.getTotal())
                .build();
    }

    private QuoteDto toQuoteDto(Quote quote) {
        return QuoteDto.builder()
                .id(quote.getId())
                .quoteNumber(quote.getQuoteNumber())
                .clientId(quote.getClient().getId())
                .clientName(quote.getClient().getCompanyName())
                .date(quote.getDate())
                .validUntil(quote.getValidUntil())
                .status(quote.getStatus())
                .subtotal(quote.getSubtotal())
                .taxRate(quote.getTaxRate())
                .taxAmount(quote.getTaxAmount())
                .total(quote.getTotal())
                .notes(quote.getNotes())
                .items(quote.getItems() != null ? quote.getItems().stream()
                        .map(this::toQuoteItemDto)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    private InvoiceDto toInvoiceDto(Invoice invoice) {
        return InvoiceDto.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .clientId(invoice.getClient().getId())
                .clientName(invoice.getClient().getCompanyName())
                .date(invoice.getDate())
                .dueDate(invoice.getDueDate())
                .paidDate(invoice.getPaidDate())
                .status(invoice.getStatus())
                .subtotal(invoice.getSubtotal())
                .taxRate(invoice.getTaxRate())
                .taxAmount(invoice.getTaxAmount())
                .total(invoice.getTotal())
                .notes(invoice.getNotes())
                .items(invoice.getItems() != null ? invoice.getItems().stream()
                        .map(this::toInvoiceItemDto)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    private ProjectDto toProjectDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .reference(project.getReference())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .statusDisplayName(project.getStatus() != null ? project.getStatus().getDisplayName() : null)
                .siteAddress(project.getSiteAddress())
                .siteCity(project.getSiteCity())
                .sitePostalCode(project.getSitePostalCode())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .progressPercentage(project.getProgressPercentage())
                .build();
    }

    private String getQuoteStatusDisplayName(QuoteStatus status) {
        if (status == null) return null;
        return switch (status) {
            case DRAFT -> "Brouillon";
            case SENT -> "Envoyé";
            case ACCEPTED -> "Accepté";
            case REJECTED -> "Refusé";
            case EXPIRED -> "Expiré";
            case CONVERTED -> "Converti en facture";
        };
    }

    private String getInvoiceStatusDisplayName(InvoiceStatus status) {
        if (status == null) return null;
        return switch (status) {
            case DRAFT -> "Brouillon";
            case SENT -> "Envoyée";
            case PAID -> "Payée";
            case OVERDUE -> "En retard";
            case CANCELLED -> "Annulée";
            case PARTIALLY_PAID -> "Partiellement payée";
        };
    }
}

