package com.mit_project.cms.config;

import com.mit_project.cms.model.Role;
import com.mit_project.cms.model.User;
import com.mit_project.cms.repository.RoleRepository;
import com.mit_project.cms.repository.UserRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Fills the database with sample data when the application starts.
 *
 * It only inserts data if the tables are empty, so restarting the
 * application never creates duplicates.
 */
@Component   // Registers this class as a Spring bean so Spring creates and runs it
@Order(1)    // Runs before RepositoryCheckRunner (@Order(2)), so the data exists first
public class DataSeeder implements CommandLineRunner {   // CommandLineRunner = "run once after startup"

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    // Constructor injection: Spring passes in the repositories automatically
    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    // Spring calls this method once, right after the application has started
    @Override
    public void run(String... args) {

        // Step 1: create the roles, but only if the roles table is empty
        if (roleRepository.count() == 0) {
            roleRepository.saveAll(List.of(
                    new Role("ADMIN"), new Role("MANAGER"), new Role("USER")));
        }

        // Step 2: create the users, but only if the users table is empty
        if (userRepository.count() == 0) {

            // Load the saved roles. Each user needs a real Role object,
            // because a user is linked to its role through role_id
            Role admin = roleRepository.findByName("ADMIN").orElseThrow();
            Role manager = roleRepository.findByName("MANAGER").orElseThrow();
            Role user = roleRepository.findByName("USER").orElseThrow();

            // Create the sample users and save them all in one go
            userRepository.saveAll(List.of(
                    new User("Alice Perera", "alice.perera@example.com", admin),
                    new User("Nimal Fernando", "nimal.fernando@example.com", manager),
                    new User("Kasun Silva", "kasun.silva@example.com", user),
                    new User("Dilani Jayawardena", "dilani.j@example.com", user),
                    new User("Ruwan Bandara", "ruwan.bandara@example.com", user)));
        }
    }
}