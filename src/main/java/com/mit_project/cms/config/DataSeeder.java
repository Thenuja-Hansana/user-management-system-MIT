package com.mit_project.cms.config;

import com.mit_project.cms.model.Role;
import com.mit_project.cms.model.User;
import com.mit_project.cms.repository.RoleRepository;
import com.mit_project.cms.repository.UserRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.saveAll(List.of(
                    new Role("ADMIN"), new Role("MANAGER"), new Role("USER")));
        }

        if (userRepository.count() == 0) {
            Role admin = roleRepository.findByName("ADMIN").orElseThrow();
            Role manager = roleRepository.findByName("MANAGER").orElseThrow();
            Role user = roleRepository.findByName("USER").orElseThrow();

            userRepository.saveAll(List.of(
                    new User("Alice Perera", "alice.perera@example.com", admin),
                    new User("Nimal Fernando", "nimal.fernando@example.com", manager),
                    new User("Kasun Silva", "kasun.silva@example.com", user),
                    new User("Dilani Jayawardena", "dilani.j@example.com", user),
                    new User("Ruwan Bandara", "ruwan.bandara@example.com", user)));
        }
    }
}