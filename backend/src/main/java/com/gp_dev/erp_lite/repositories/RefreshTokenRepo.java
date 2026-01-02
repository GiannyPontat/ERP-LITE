package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.RefreshToken;
import com.gp_dev.erp_lite.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
    Optional<RefreshToken> findByUserAndRevokedFalse(User user);
}
