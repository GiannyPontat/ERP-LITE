package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.TokenType;
import com.gp_dev.erp_lite.models.User;
import com.gp_dev.erp_lite.models.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepo extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUserAndTypeAndUsedFalse(User user, TokenType type);
    void deleteByUser(User user);
}
