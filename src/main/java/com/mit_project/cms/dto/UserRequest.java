package com.mit_project.cms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data coming IN from the client when creating or updating a user.
 * It has no id or createdAt, so the client cannot set them.
 * The validation rules are checked when the controller uses @Valid.
 */
public class UserRequest {

    // Required, cannot be only spaces, max 100 characters (same as the database column)
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    // Required, must look like an email, max 150 characters
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 150, message = "Email must be at most 150 characters")
    private String email;

    // The id of the role (1 = ADMIN, 2 = MANAGER, 3 = USER). Required.
    @NotNull(message = "Role is required")
    private Long roleId;

    // Empty constructor: needed to turn the JSON into this object
    public UserRequest() {
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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}