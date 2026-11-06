package com.mit_project.cms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A role, for example ADMIN, MANAGER or USER.
 * This class maps to the "roles" table in the database.
 * One role can be used by many users.
 */
@Entity                  // This class is a database table
@Table(name = "roles")   // The table name
public class Role {

    @Id                                                    // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)    // The database numbers it automatically
    private Long id;

    // Required, must be unique, max 50 characters
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // Empty constructor: required by JPA
    public Role() {
    }

    // Convenient constructor, used when we create roles in code
    public Role(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}