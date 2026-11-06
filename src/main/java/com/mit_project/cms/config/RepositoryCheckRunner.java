package com.mit_project.cms.config;

import com.mit_project.cms.model.User;
import com.mit_project.cms.repository.RoleRepository;
import com.mit_project.cms.repository.UserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * A test helper. It runs once when the app starts and prints
 * the results of our repository methods in the console.
 * We will delete it during the Day 6 cleanup.
 */
@Component   // Spring creates this class automatically
@Order(2)    // Runs after DataSeeder (@Order(1)), so the data is already there
public class RepositoryCheckRunner implements CommandLineRunner {   // Runs once after startup

    // Used to print messages in the console
    private static final Logger log = LoggerFactory.getLogger(RepositoryCheckRunner.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // Spring passes in the repositories (constructor injection)
    public RepositoryCheckRunner(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // Spring calls this method once, after the app has started
    @Override
    public void run(String... args) {
        log.info("=== Repository check ===");

        // Check 1: count the rows in each table
        log.info("Roles: {}", roleRepository.count());
        log.info("Users: {}", userRepository.count());

        // Check 2: find one user by email and print the name and role
        userRepository.findByEmail("alice.perera@example.com")
                .ifPresent(u -> log.info("findByEmail: {} ({})", u.getName(), u.getRole().getName()));

        // Check 3: find users whose name contains "an"
        log.info("findByNameContaining(an): {}", names(userRepository.findByNameContaining("an")));

        // Check 4: find users with the role USER (custom @Query)
        log.info("findByRoleName(USER): {}", names(userRepository.findByRoleName("USER")));
    }

    // Helper: turns a list of users into a list of names, so the log is easy to read
    private List<String> names(List<User> users) {
        return users.stream().map(User::getName).toList();
    }
}