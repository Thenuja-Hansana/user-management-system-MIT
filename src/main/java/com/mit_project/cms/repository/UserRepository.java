package com.mit_project.cms.repository;

import com.mit_project.cms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}