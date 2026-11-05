package com.mit_project.cms.dto;

import java.time.LocalDateTime;

public class UserResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final Long roleId;
    private final String roleName;
    private final LocalDateTime createdAt;

    public UserResponse(Long id, String name, String email,
                        Long roleId, String roleName, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roleId = roleId;
        this.roleName = roleName;
        this.createdAt = createdAt;
    }

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