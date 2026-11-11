// [NEW FILE - login] Added in the Login update.
package com.mit_project.cms.config;

import com.mit_project.cms.model.Role;
import com.mit_project.cms.model.Status;
import com.mit_project.cms.model.User;
import com.mit_project.cms.repository.RoleRepository;
import com.mit_project.cms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Makes sure there is always at least one admin who can log in.
 *
 * Runs once when the application starts (after DataSeeder). If there is NO active admin
 * with a password, it:
 *   1. re-activates the ADMIN role if it is inactive, and
 *   2. creates the default admin from application.properties (app.admin.*),
 *      or repairs that user if they already exist (ADMIN role, ACTIVE, password reset).
 * If an admin who can log in already exists, it does nothing.
 */
@Component
@Order(2)   // after DataSeeder (@Order(1)), so the roles exist
public class AdminAccountInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminAccountInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // @Value reads the values from application.properties
    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.name:System Admin}")   // ":System Admin" = default if the setting is missing
    private String adminName;

    public AdminAccountInitializer(UserRepository userRepository, RoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName(Role.ADMIN).orElseThrow();

        // Is there already an admin who can log in? Then there is nothing to do.
        boolean adminCanLogIn = adminRole.getStatus() == Status.ACTIVE
                && userRepository.existsByRoleAndStatusAndPasswordHashIsNotNull(adminRole, Status.ACTIVE);
        if (adminCanLogIn) {
            return;
        }

        // Step 1: the ADMIN role must be active
        if (adminRole.getStatus() != Status.ACTIVE) {
            adminRole.setStatus(Status.ACTIVE);
            roleRepository.save(adminRole);
        }

        // Step 2: create the default admin, or repair them if the email already exists
        User admin = userRepository.findByEmail(adminEmail)
                .orElseGet(() -> new User(adminName, adminEmail, adminRole));
        admin.setRole(adminRole);
        admin.setStatus(Status.ACTIVE);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        userRepository.save(admin);

        log.warn("No admin could log in, so the default admin account was set up: {}. "
                + "Log in and change its password.", adminEmail);
    }
}
