package com.gp_dev.erp_lite.controllers;

import com.gp_dev.erp_lite.dtos.*;
import com.gp_dev.erp_lite.services.AuthService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "User authentication and account management endpoints")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/auth", produces = "application/json; charset=UTF-8")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Creates a new user account and sends verification email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @Operation(summary = "User login", description = "Authenticates user and returns JWT access token and refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Email not verified",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request received");
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @Operation(summary = "User logout", description = "Revokes all refresh tokens for the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Logout request received for user: {}", userDetails.getUsername());
        authService.logout(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verify email address", description = "Verifies user email using the token sent via email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        log.info("Email verification request received");
        return ResponseEntity.ok(authService.verifyEmail(request.getToken()));
    }

    @Operation(summary = "Request password reset", description = "Sends a password reset link to the user's email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reset email sent",
            content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Password reset request received for email: {}", request.getEmail());
        return ResponseEntity.ok(authService.forgotPassword(request.getEmail()));
    }

    @Operation(summary = "Reset password", description = "Resets user password using the token from reset email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Password reset confirmation request received");
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @Operation(summary = "Resend verification email", description = "Sends a new verification email to the user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verification email sent",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resendVerification(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Resend verification request received for email: {}", request.getEmail());
        return ResponseEntity.ok(authService.resendVerificationEmail(request.getEmail()));
    }
}
