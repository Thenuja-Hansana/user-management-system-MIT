package com.mit_project.cms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;                    // [ADDED]
import jakarta.persistence.Enumerated;                  // [ADDED]
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;                  // [ADDED]
import jakarta.persistence.PreUpdate;                   // [ADDED]
import jakarta.persistence.Table;
import java.time.LocalDateTime;                         // [ADDED]
import org.hibernate.annotations.ColumnDefault;         // [ADDED]

/**
 * A role, for example ADMIN, MANAGER, USER or GUEST.
 * This class maps to the "roles" table in the database.
 * One role can be used by many users.
 */
@Entity                  // This class is a database table
@Table(name = "roles")   // The table name
public class Role {

    // [ADDED] GUEST constant
    // Name of the fallback role. When a role becomes inactive,
    // its users are moved to this role. GUEST itself can never be inactive.
    public static final String GUEST = "GUEST";

    @Id                                                    // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)    // The database numbers it automatically
    private Long id;

    // Required, must be unique, max 50 characters
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // [ADDED] status field
    // ACTIVE or INACTIVE, stored as text. New roles are active.
    // @ColumnDefault gives existing rows a value when the column is added.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @ColumnDefault("'ACTIVE'")
    private Status status = Status.ACTIVE;

    // [ADDED] updatedAt field
    // Last time this role was changed. Set automatically (see below).
    @Column(name = "updated_at", nullable = false)
    @ColumnDefault("SYSDATETIME()")
    private LocalDateTime updatedAt;

    // Empty constructor: required by JPA
    public Role() {
    }

    // Convenient constructor, used when we create roles in code
    public Role(String name) {
        this.name = name;
    }

    // [ADDED] sets updatedAt automatically
    // Runs before the first save and before every update
    @PrePersist
    @PreUpdate
    void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    // [ADDED] helper
    // true for the GUEST role
    public boolean isGuest() {
        return GUEST.equals(name);
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

    // [ADDED] getters/setter for status and updatedAt
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // No setter: updatedAt is set automatically
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
