package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.*;
import com.gp_dev.erp_lite.services.ClientPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Client Portal", description = "Client portal authentication and access endpoints")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/client-portal")
public class ClientPortalController {

    private final ClientPortalService clientPortalService;

    // === Authentication Endpoints ===

    @Operation(summary = "Client portal login", description = "Authenticates a client and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = ClientPortalAuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/auth/login")
    public ResponseEntity<ClientPortalAuthResponse> login(@Valid @RequestBody ClientPortalLoginRequest request) {
        log.info("Client portal login attempt for: {}", request.getEmail());
        return ResponseEntity.ok(clientPortalService.login(request));
    }

    @Operation(summary = "Verify email", description = "Verifies client portal email with token")
    @GetMapping("/auth/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
        clientPortalService.verifyEmail(token);
        return ResponseEntity.ok(new MessageResponse("Email verified successfully"));
    }

    @Operation(summary = "Request password reset", description = "Sends a password reset email")
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestParam String email) {
        clientPortalService.requestPasswordReset(email);
        return ResponseEntity.ok(new MessageResponse("Password reset email sent"));
    }

    @Operation(summary = "Reset password", description = "Resets password with token")
    @PostMapping("/auth/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        clientPortalService.resetPassword(token, newPassword);
        return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
    }

    // === Quote Endpoints ===

    @Operation(summary = "Get client quotes", description = "Retrieves all quotes for the authenticated client",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/clients/{clientId}/quotes")
    public ResponseEntity<List<ClientPortalQuoteDto>> getQuotes(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientPortalService.getClientQuotes(clientId));
    }

    @Operation(summary = "Get quote details", description = "Retrieves detailed information about a specific quote",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/clients/{clientId}/quotes/{quoteId}")
    public ResponseEntity<ClientPortalQuoteDto> getQuote(
            @PathVariable Long clientId,
            @PathVariable Long quoteId) {
        return ResponseEntity.ok(clientPortalService.getQuote(clientId, quoteId));
    }

    @Operation(summary = "Accept quote", description = "Accepts a quote (changes status to ACCEPTED)",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/clients/{clientId}/quotes/{quoteId}/accept")
    public ResponseEntity<ClientPortalQuoteDto> acceptQuote(
            @PathVariable Long clientId,
            @PathVariable Long quoteId) {
        log.info("Client {} accepting quote {}", clientId, quoteId);
        return ResponseEntity.ok(clientPortalService.acceptQuote(clientId, quoteId));
    }

    @Operation(summary = "Reject quote", description = "Rejects a quote (changes status to REJECTED)",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/clients/{clientId}/quotes/{quoteId}/reject")
    public ResponseEntity<ClientPortalQuoteDto> rejectQuote(
            @PathVariable Long clientId,
            @PathVariable Long quoteId) {
        log.info("Client {} rejecting quote {}", clientId, quoteId);
        return ResponseEntity.ok(clientPortalService.rejectQuote(clientId, quoteId));
    }

    @Operation(summary = "Download quote PDF", description = "Downloads the PDF version of a quote",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/clients/{clientId}/quotes/{quoteId}/pdf")
    public ResponseEntity<byte[]> downloadQuotePdf(
            @PathVariable Long clientId,
            @PathVariable Long quoteId) {
        byte[] pdfBytes = clientPortalService.downloadQuotePdf(clientId, quoteId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"devis.pdf\"");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    // === Invoice Endpoints ===

    @Operation(summary = "Get client invoices", description = "Retrieves all invoices for the authenticated client",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/clients/{clientId}/invoices")
    public ResponseEntity<List<ClientPortalInvoiceDto>> getInvoices(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientPortalService.getClientInvoices(clientId));
    }

    @Operation(summary = "Get invoice details", description = "Retrieves detailed information about a specific invoice",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/clients/{clientId}/invoices/{invoiceId}")
    public ResponseEntity<ClientPortalInvoiceDto> getInvoice(
            @PathVariable Long clientId,
            @PathVariable Long invoiceId) {
        return ResponseEntity.ok(clientPortalService.getInvoice(clientId, invoiceId));
    }

    @Operation(summary = "Download invoice PDF", description = "Downloads the PDF version of an invoice",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/clients/{clientId}/invoices/{invoiceId}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(
            @PathVariable Long clientId,
            @PathVariable Long invoiceId) {
        byte[] pdfBytes = clientPortalService.downloadInvoicePdf(clientId, invoiceId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"facture.pdf\"");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    // === Project Endpoints ===

    @Operation(summary = "Get client projects", description = "Retrieves all projects for the authenticated client",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/clients/{clientId}/projects")
    public ResponseEntity<List<ProjectDto>> getProjects(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientPortalService.getClientProjects(clientId));
    }

    @Operation(summary = "Get project details", description = "Retrieves detailed information about a specific project",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/clients/{clientId}/projects/{projectId}")
    public ResponseEntity<ProjectDto> getProject(
            @PathVariable Long clientId,
            @PathVariable Long projectId) {
        return ResponseEntity.ok(clientPortalService.getProject(clientId, projectId));
    }

    // === Admin Endpoints (for managing portal access) ===

    @Operation(summary = "Create portal access", description = "Creates portal access for a client (admin only)",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/admin/create-access")
    public ResponseEntity<MessageResponse> createPortalAccess(@Valid @RequestBody ClientPortalRegisterRequest request) {
        clientPortalService.createPortalAccess(request);
        return ResponseEntity.ok(new MessageResponse("Portal access created. Verification email sent."));
    }

    @Operation(summary = "Check portal access", description = "Checks if a client has portal access (admin only)",
        security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/admin/clients/{clientId}/has-access")
    public ResponseEntity<Boolean> hasPortalAccess(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientPortalService.hasPortalAccess(clientId));
    }

    @Operation(summary = "Disable portal access", description = "Disables portal access for a client (admin only)",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/admin/clients/{clientId}/disable")
    public ResponseEntity<MessageResponse> disablePortalAccess(@PathVariable Long clientId) {
        clientPortalService.disablePortalAccess(clientId);
        return ResponseEntity.ok(new MessageResponse("Portal access disabled"));
    }

    @Operation(summary = "Enable portal access", description = "Enables portal access for a client (admin only)",
        security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/admin/clients/{clientId}/enable")
    public ResponseEntity<MessageResponse> enablePortalAccess(@PathVariable Long clientId) {
        clientPortalService.enablePortalAccess(clientId);
        return ResponseEntity.ok(new MessageResponse("Portal access enabled"));
    }
}

