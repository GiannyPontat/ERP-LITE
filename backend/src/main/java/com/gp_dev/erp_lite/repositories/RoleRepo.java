package com.gp_dev.erp_lite.repositories;

import com.gp_dev.erp_lite.models.Role;
import com.gp_dev.erp_lite.models.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
