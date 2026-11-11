package com.mit_project.cms.dto;

import com.mit_project.cms.model.Status;   // [ADDED]
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;   // [ADDED - password]
import jakarta.validation.constraints.Size;

/**
 * Data coming IN from the client when creating or updating a user.
 * It has no id, createdAt or updatedAt, so the client cannot set them.
 * The validation rules are checked when the controller uses @Valid.
 */
public class UserRequest {

    // [ADDED - password] the password rules, written as a regular expression:
    // (?=.*[A-Z])        at least one uppercase letter
    // (?=.*[a-z])        at least one lowercase letter
    // (?=.*\d)           at least one number
    // (?=.*[^A-Za-z0-9]) at least one special character (anything that is not a letter or number)
    // The length (6+) is checked by @Size below. The web page checks the same rules while you type.
    public static final String PASSWORD_RULES =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$";

    // Required, cannot be only spaces, max 100 characters (same as the database column)
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    // Required, must look like an email, max 150 characters
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 150, message = "Email must be at most 150 characters")
    private String email;

    // [CHANGED] comment: the role must now be active
    // The id of the role (see GET /api/roles). Required, and the role must be active.
    @NotNull(message = "Role is required")
    private Long roleId;

    // [ADDED] status field
    // ACTIVE or INACTIVE. Optional: if missing, a new user is ACTIVE
    // and an updated user keeps the status they already have.
    private Status status;

    // [ADDED - password] the real password typed by the user (it is hashed before saving).
    // Required when CREATING a user (UserService checks this).
    // Optional when EDITING: leave it out to keep the current password.
    // If it is sent, it must follow the rules. Max 72 because BCrypt only uses the first 72 bytes.
    @Size(min = 6, max = 72, message = "Password must be 6 to 72 characters")
    @Pattern(regexp = PASSWORD_RULES,
            message = "Password must contain an uppercase letter, a lowercase letter, a number and a special character")
    private String password;

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

    // [ADDED] getter/setter for status
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // [ADDED - password] getter/setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
