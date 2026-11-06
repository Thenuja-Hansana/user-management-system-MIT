package com.mit_project.cms.dto;

import java.time.LocalDateTime;

/**
 * Data going OUT to the client when we return a user.
 * We return this instead of the User entity, so the API
 * stays the same even if the database tables change.
 * All fields are final, so the object cannot be changed after it is created.
 */
public class UserResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final Long roleId;        // Id of the user's role
    private final String roleName;    // Name of the role, for example ADMIN
    private final LocalDateTime createdAt;

    // All values are set here, when the object is created
    public UserResponse(Long id, String name, String email,
                        Long roleId, String roleName, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roleId = roleId;
        this.roleName = roleName;
        this.createdAt = createdAt;
    }

    // Only getters, no setters (read-only). Spring uses them to build the JSON.
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}