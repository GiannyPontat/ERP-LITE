package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.RefreshToken;
import com.gp_dev.erp_lite.models.User;
import com.gp_dev.erp_lite.repositories.RefreshTokenRepo;
import com.gp_dev.erp_lite.repositories.UserRepo;
import com.gp_dev.erp_lite.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    @Override
    public RefreshToken createRefreshToken(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .revoked(false)
                .user(user)
                .build();

        return refreshTokenRepo.saveAndFlush(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepo.delete(token);
            throw new AppException("Refresh token expired. Please login again", HttpStatus.UNAUTHORIZED);
        }
        if (token.getRevoked()) {
            throw new AppException("Refresh token has been revoked. Please login again", HttpStatus.UNAUTHORIZED);
        }
        return token;
    }

    @Override
    @Transactional
    public void revokeTokensByUser(User user) {
        refreshTokenRepo.deleteByUser(user);
    }
}
