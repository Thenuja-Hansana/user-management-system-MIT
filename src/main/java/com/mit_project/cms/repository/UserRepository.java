package com.mit_project.cms.repository;

import com.mit_project.cms.model.Role;                                  // [ADDED]
import com.mit_project.cms.model.User;
import java.time.LocalDateTime;                                         // [ADDED]
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;               // [ADDED]
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Database access for users.
 * We only write the method names. Spring creates the code and the SQL for us.
 * JpaRepository already gives us save, delete, count and more.
 *
 * About @EntityGraph(attributePaths = "role"):
 * it loads the user and the role in ONE query (a join).
 * Without it, Spring would run one extra query per user to load the role (N+1 problem).
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // Replaces the built-in findAll() so the role is loaded together with each user
    @Override
    @EntityGraph(attributePaths = "role")
    List<User> findAll();

    // Replaces the built-in findById() for the same reason
    @Override
    @EntityGraph(attributePaths = "role")
    Optional<User> findById(Long id);

    // Derived query: Spring reads the method name. WHERE email = ?
    @EntityGraph(attributePaths = "role")
    Optional<User> findByEmail(String email);

    // true if a user with this email exists. Used to block duplicate emails when creating.
    boolean existsByEmail(String email);

    // true if ANOTHER user has this email. Used when updating, so a user can keep their own email.
    boolean existsByEmailAndIdNot(String email, Long id);

    // Derived query: WHERE name LIKE %?%
    @EntityGraph(attributePaths = "role")
    List<User> findByNameContaining(String name);

    // Search: name OR email contains the text. This is used by the search box.
    @EntityGraph(attributePaths = "role")
    List<User> findByNameContainingOrEmailContaining(String name, String email);

    // Custom query written by us (JPQL). It uses class names (User), not table names.
    // JOIN FETCH loads the role in the same query.
    // :roleName is filled by @Param("roleName").
    @Query("SELECT u FROM User u JOIN FETCH u.role r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    // [ADDED] count users per role
    // How many users have this role. Shown in the roles table.
    long countByRole(Role role);

    // [ADDED] move users to another role (used to move them to GUEST)
    // Moves every user of one role to another role in a single UPDATE statement.
    // Used when a role becomes inactive (its users go to GUEST).
    // A bulk update skips @PreUpdate, so updatedAt is set here directly.
    // clearAutomatically: forget cached users afterwards, so later reads see the new role.
    // Returns the number of users that were moved.
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.role = :to, u.updatedAt = :now WHERE u.role = :from")
    int reassignRole(@Param("from") Role from, @Param("to") Role to, @Param("now") LocalDateTime now);
}
