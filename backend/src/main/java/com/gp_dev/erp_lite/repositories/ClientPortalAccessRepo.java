package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.ClientPortalAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientPortalAccessRepo extends JpaRepository<ClientPortalAccess, Long> {

    Optional<ClientPortalAccess> findByEmail(String email);

    Optional<ClientPortalAccess> findByEmailAndActiveTrue(String email);

    boolean existsByEmail(String email);

    List<ClientPortalAccess> findByClientId(Long clientId);

    Optional<ClientPortalAccess> findByVerificationToken(String token);

    Optional<ClientPortalAccess> findByResetToken(String token);

    void deleteByClientId(Long clientId);
}

