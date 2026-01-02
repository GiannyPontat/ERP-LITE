package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.TokenType;
import com.gp_dev.erp_lite.models.User;
import com.gp_dev.erp_lite.models.VerificationToken;
import com.gp_dev.erp_lite.repositories.VerificationTokenRepo;
import com.gp_dev.erp_lite.services.VerificationTokenService;
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
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepo verificationTokenRepo;

    @Value("${verification.token.expiration}")
    private Long verificationTokenExpiration;

    @Value("${reset.token.expiration}")
    private Long resetTokenExpiration;

    @Override
    public VerificationToken createToken(User user, TokenType type) {
        Long expirationMs = type == TokenType.EMAIL_VERIFICATION
                ? verificationTokenExpiration
                : resetTokenExpiration;

        VerificationToken token = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusSeconds(expirationMs / 1000))
                .used(false)
                .type(type)
                .user(user)
                .build();

        return verificationTokenRepo.saveAndFlush(token);
    }

    @Override
    public VerificationToken verifyToken(String tokenString, TokenType type) {
        VerificationToken token = verificationTokenRepo.findByToken(tokenString)
                .orElseThrow(() -> new AppException("Invalid token", HttpStatus.BAD_REQUEST));

        if (!token.getType().equals(type)) {
            throw new AppException("Invalid token type", HttpStatus.BAD_REQUEST);
        }

        if (token.getUsed()) {
            throw new AppException("Token has already been used", HttpStatus.BAD_REQUEST);
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AppException("Token has expired", HttpStatus.BAD_REQUEST);
        }

        return token;
    }

    @Override
    @Transactional
    public void deleteTokensByUser(User user) {
        verificationTokenRepo.deleteByUser(user);
    }
}
