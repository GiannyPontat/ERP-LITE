package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.*;
import com.gp_dev.erp_lite.models.TokenType;
import com.gp_dev.erp_lite.models.VerificationToken;
import com.gp_dev.erp_lite.services.EmailService;
import com.gp_dev.erp_lite.services.VerificationTokenService;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.RefreshToken;
import com.gp_dev.erp_lite.models.Role;
import com.gp_dev.erp_lite.models.RoleType;
import com.gp_dev.erp_lite.models.User;
import com.gp_dev.erp_lite.repositories.RefreshTokenRepo;
import com.gp_dev.erp_lite.repositories.RoleRepo;
import com.gp_dev.erp_lite.repositories.UserRepo;
import com.gp_dev.erp_lite.security.JwtUtil;
import com.gp_dev.erp_lite.services.AuthService;
import com.gp_dev.erp_lite.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Log4j2
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration;

    @Override
    public MessageResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new AppException("Email already registered", HttpStatus.BAD_REQUEST);
        }

        // Find USER role
        Role userRole = roleRepo.findByName(RoleType.USER)
                .orElseThrow(() -> new AppException("Role USER not found", HttpStatus.INTERNAL_SERVER_ERROR));

        // Create and save user (not verified, not enabled yet)
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .enabled(false)
                .emailVerified(false)
                .roles(Set.of(userRole))
                .build();

        user = userRepo.saveAndFlush(user);

        // Create verification token and send email
        VerificationToken verificationToken = verificationTokenService.createToken(user, TokenType.EMAIL_VERIFICATION);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken.getToken());

        return MessageResponse.builder()
                .message("Registration successful! Please check your email to verify your account.")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new AppException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        // Load user
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        // Check if email is verified
        if (!user.getEmailVerified()) {
            throw new AppException("Email not verified. Please check your email.", HttpStatus.FORBIDDEN);
        }

        // Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);

        // Revoke old refresh tokens and create new one
        refreshTokenService.revokeTokensByUser(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .user(user.dto())
                .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshTokenString) {
        // Find refresh token in database
        RefreshToken refreshToken = refreshTokenRepo.findByToken(refreshTokenString)
                .orElseThrow(() -> new AppException("Refresh token not found", HttpStatus.NOT_FOUND));

        // Verify expiration and revocation
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);

        // Get user and generate new access token
        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .user(user.dto())
                .build();
    }

    @Override
    public void logout(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        // Revoke all refresh tokens for the user
        refreshTokenService.revokeTokensByUser(user);
    }

    @Override
    public MessageResponse verifyEmail(String tokenString) {
        // Verify token
        VerificationToken token = verificationTokenService.verifyToken(tokenString, TokenType.EMAIL_VERIFICATION);

        // Get user and update verification status
        User user = token.getUser();
        user.setEmailVerified(true);
        user.setEnabled(true);
        userRepo.save(user);

        // Mark token as used
        token.setUsed(true);
        verificationTokenService.deleteTokensByUser(user);

        return MessageResponse.builder()
                .message("Email verified successfully! You can now login.")
                .build();
    }

    @Override
    public MessageResponse forgotPassword(String email) {
        // Find user - return success even if not found (security: don't reveal if email exists)
        User user = userRepo.findByEmail(email).orElse(null);

        if (user != null) {
            // Delete old password reset tokens
            verificationTokenService.deleteTokensByUser(user);

            // Create new password reset token
            VerificationToken resetToken = verificationTokenService.createToken(user, TokenType.PASSWORD_RESET);

            // Send password reset email
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
        }

        return MessageResponse.builder()
                .message("If the email exists, a password reset link has been sent.")
                .build();
    }

    @Override
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        // Verify token
        VerificationToken token = verificationTokenService.verifyToken(request.getToken(), TokenType.PASSWORD_RESET);

        // Get user and update password
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        // Mark token as used
        token.setUsed(true);
        verificationTokenService.deleteTokensByUser(user);

        // Revoke all refresh tokens (logout user from all devices)
        refreshTokenService.revokeTokensByUser(user);

        // Send password change confirmation email
        emailService.sendPasswordChangeConfirmation(user.getEmail());

        return MessageResponse.builder()
                .message("Password reset successfully! Please login with your new password.")
                .build();
    }

    @Override
    public MessageResponse resendVerificationEmail(String email) {
        // Find user
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        // Check if already verified
        if (user.getEmailVerified()) {
            throw new AppException("Email already verified", HttpStatus.BAD_REQUEST);
        }

        // Delete old verification tokens
        verificationTokenService.deleteTokensByUser(user);

        // Create new verification token
        VerificationToken verificationToken = verificationTokenService.createToken(user, TokenType.EMAIL_VERIFICATION);

        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), verificationToken.getToken());

        return MessageResponse.builder()
                .message("Verification email has been resent. Please check your inbox.")
                .build();
    }
}
