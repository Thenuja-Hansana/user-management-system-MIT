package com.mit_project.cms.repository;

import com.mit_project.cms.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Database access for roles.
 * JpaRepository<Role, Long> means: this repository works with the Role entity,
 * and the id type is Long. Spring creates the code for us.
 * It already gives us save, findById, findAll, delete, count and more.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Derived query: WHERE name = ?
    // Optional means the role may not exist (no result)
    Optional<Role> findByName(String name);
}