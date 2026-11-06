package com.mit_project.cms.mapper;

import com.mit_project.cms.dto.UserRequest;
import com.mit_project.cms.dto.UserResponse;
import com.mit_project.cms.model.Role;
import com.mit_project.cms.model.User;
import org.springframework.stereotype.Component;

/**
 * Converts between the User entity (database) and the DTOs (API).
 * This keeps the conversion code in one place.
 */
@Component   // Spring creates it, so we can inject it into UserService
public class UserMapper {

    // Entity -> response. Used when we send a user back to the client.
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getId(),      // Role id
                user.getRole().getName(),    // Role name, for example ADMIN
                user.getCreatedAt());
    }

    // Request -> new entity. Used when creating a user.
    // trim() removes extra spaces at the start and end.
    // The role is passed in because the request only has a roleId.
    public User toEntity(UserRequest request, Role role) {
        return new User(request.getName().trim(), request.getEmail().trim(), role);
    }

    // Request -> existing entity. Used when updating a user.
    // It changes the values of the user that is already loaded from the database.
    public void updateEntity(User user, UserRequest request, Role role) {
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim());
        user.setRole(role);
    }
}