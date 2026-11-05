package com.mit_project.cms.mapper;

import com.mit_project.cms.dto.UserRequest;
import com.mit_project.cms.dto.UserResponse;
import com.mit_project.cms.model.Role;
import com.mit_project.cms.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getId(),
                user.getRole().getName(),
                user.getCreatedAt());
    }

    public User toEntity(UserRequest request, Role role) {
        return new User(request.getName().trim(), request.getEmail().trim(), role);
    }

    public void updateEntity(User user, UserRequest request, Role role) {
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim());
        user.setRole(role);
    }
}