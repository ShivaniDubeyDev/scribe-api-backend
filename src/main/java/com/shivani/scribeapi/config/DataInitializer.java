package com.shivani.scribeapi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shivani.scribeapi.entity.Role;
import com.shivani.scribeapi.repository.RoleRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Programmatic database initializer component that seeds foundational security roles
 * during the application context startup lifecycle phase.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepo roleRepo;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            log.info("Executing database lookup metadata seeding lifecycle tasks.");

            // Check and initialize ADMIN role
            if (!this.roleRepo.existsById(AppConstants.ADMIN_USER)) {
                Role adminRole = new Role();
                adminRole.setId(AppConstants.ADMIN_USER);
                adminRole.setName("ROLE_ADMIN");
                this.roleRepo.save(adminRole);
                log.debug("Foundational authorization role successfully seeded: ROLE_ADMIN");
            }

            // Check and initialize NORMAL role
            if (!this.roleRepo.existsById(AppConstants.NORMAL_USER)) {
                Role normalRole = new Role();
                normalRole.setId(AppConstants.NORMAL_USER);
                normalRole.setName("ROLE_NORMAL");
                this.roleRepo.save(normalRole);
                log.debug("Foundational authorization role successfully seeded: ROLE_NORMAL");
            }
            
            log.info("Database security role metadata initialization completed successfully.");
        } catch (Exception e) {
            log.error("An unhandled exception anomaly occurred during startup database seeding sequences.", e);
        }
    }
}
