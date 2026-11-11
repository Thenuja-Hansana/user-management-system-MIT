package com.mit_project.cms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;                    // [ADDED]
import jakarta.persistence.Enumerated;                  // [ADDED]
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;                   // [ADDED]
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import jakarta.persistence.FetchType;
import org.hibernate.annotations.ColumnDefault;         // [ADDED]

/**
 * A user of the system.
 * This class maps to the "users" table in the database.
 * Each user has exactly one role.
 */
@Entity                  // This class is a database table
@Table(name = "users")   // Table name. "user" is a reserved word in SQL Server, so we use "users".
public class User {

    @Id                                                    // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)    // The database numbers it automatically
    private Long id;

    // Required, max 100 characters
    @Column(nullable = false, length = 100)
    private String name;

    // Required, must be unique, max 150 characters
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // [ADDED - password] the HASHED password (BCrypt), never the real password.
    // Can be empty (null) for older users until someone sets a password for them.
    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    // Set once when the user is created, and never changed after that
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // [ADDED] updatedAt field
    // Last time this user was changed. Set automatically (see below).
    @Column(name = "updated_at", nullable = false)
    @ColumnDefault("SYSDATETIME()")
    private LocalDateTime updatedAt;

    // [ADDED] status field
    // ACTIVE or INACTIVE, stored as text. New users are active.
    // @ColumnDefault gives existing rows a value when the column is added.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @ColumnDefault("'ACTIVE'")
    private Status status = Status.ACTIVE;

    // Many users can have one role (many-to-one).
    // LAZY: the role is loaded only when needed. The repository loads it together
    // with the user (entity graph), which avoids the extra queries (N+1 problem).
    // The "role_id" column in the users table is the foreign key to roles.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Empty constructor: required by JPA
    public User() {
    }

    // Convenient constructor, used when we create a new user in code
    public User(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Runs automatically just before the user is saved for the first time.
    // It sets the creation time, so we never have to set it ourselves.
    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;   // [ADDED]
    }

    // [ADDED] sets updatedAt automatically on every change
    // Runs automatically just before a changed user is saved
    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // [ADDED - password] getter/setter for the password hash
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // No setter for createdAt: it is set automatically and must not be changed
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // [ADDED] getters/setter for updatedAt and status
    // No setter: updatedAt is set automatically
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
