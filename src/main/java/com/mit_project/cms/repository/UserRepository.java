package com.mit_project.cms.repository;

import com.mit_project.cms.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @EntityGraph(attributePaths = "role")
    List<User> findAll();

    @Override
    @EntityGraph(attributePaths = "role")
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = "role")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @EntityGraph(attributePaths = "role")
    List<User> findByNameContaining(String name);

    @EntityGraph(attributePaths = "role")
    List<User> findByNameContainingOrEmailContaining(String name, String email);

    @Query("SELECT u FROM User u JOIN FETCH u.role r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
}