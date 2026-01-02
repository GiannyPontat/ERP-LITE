package com.gp_dev.erp_lite.config;

import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.Role;
import com.gp_dev.erp_lite.models.RoleType;
import com.gp_dev.erp_lite.models.User;
import com.gp_dev.erp_lite.repositories.RoleRepo;
import com.gp_dev.erp_lite.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Log4j2
public class DataInitializer implements CommandLineRunner {

    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");

        // Create roles if they don't exist
        createRoleIfNotExists(RoleType.ADMIN);
        createRoleIfNotExists(RoleType.USER);

        // Create default admin user if not exists
        if (!userRepo.existsByEmail("admin@erplite.com")) {
            createDefaultAdmin();
        }

        log.info("Data initialization completed successfully");
    }

    private void createRoleIfNotExists(RoleType roleType) {
        if (roleRepo.findByName(roleType).isEmpty()) {
            Role role = Role.builder()
                    .name(roleType)
                    .build();
            roleRepo.save(role);
            log.info("Created role: {}", roleType);
        } else {
            log.info("Role already exists: {}", roleType);
        }
    }

    private void createDefaultAdmin() {
        Role adminRole = roleRepo.findByName(RoleType.ADMIN)
                .orElseThrow(() -> new AppException("Admin role not found", HttpStatus.INTERNAL_SERVER_ERROR));
        Role userRole = roleRepo.findByName(RoleType.USER)
                .orElseThrow(() -> new AppException("User role not found", HttpStatus.INTERNAL_SERVER_ERROR));

        User admin = User.builder()
                .email("admin@erplite.com")
                .password(passwordEncoder.encode("Admin@123"))
                .firstName("Admin")
                .lastName("User")
                .enabled(true)
                .emailVerified(true)
                .roles(Set.of(adminRole, userRole))
                .build();

        userRepo.save(admin);
        log.info("Created default admin user: admin@erplite.com / Admin@123");
        log.warn("IMPORTANT: Please change the default admin password after first login!");
    }
}
