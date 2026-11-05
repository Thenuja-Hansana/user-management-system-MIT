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

@Component
@Order(2)
public class RepositoryCheckRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RepositoryCheckRunner.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public RepositoryCheckRunner(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        log.info("=== Repository check ===");
        log.info("Roles: {}", roleRepository.count());
        log.info("Users: {}", userRepository.count());

        userRepository.findByEmail("alice.perera@example.com")
                .ifPresent(u -> log.info("findByEmail: {} ({})", u.getName(), u.getRole().getName()));

        log.info("findByNameContaining(an): {}", names(userRepository.findByNameContaining("an")));
        log.info("findByRoleName(USER): {}", names(userRepository.findByRoleName("USER")));
    }

    private List<String> names(List<User> users) {
        return users.stream().map(User::getName).toList();
    }
}