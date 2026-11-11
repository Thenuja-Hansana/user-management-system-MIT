package com.mit_project.cms.service;

import com.mit_project.cms.dto.UserRequest;
import com.mit_project.cms.dto.UserResponse;
import com.mit_project.cms.exception.DuplicateResourceException;
import com.mit_project.cms.exception.InvalidOperationException;   // [ADDED]
import com.mit_project.cms.exception.ResourceNotFoundException;
import com.mit_project.cms.mapper.UserMapper;
import com.mit_project.cms.model.Role;
import com.mit_project.cms.model.Status;                          // [ADDED]
import com.mit_project.cms.model.User;
import com.mit_project.cms.repository.RoleRepository;
import com.mit_project.cms.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The business logic for users.
 * Controllers call this class. This class uses the repositories and the mapper.
 * It works with DTOs, so the controllers never touch the entities.
 */
@Service                         // Spring creates one instance of this class
@Transactional(readOnly = true)  // By default, every method runs in a read-only transaction
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    // Constructor injection: Spring passes in the three objects.
    // They are final, so they cannot be null or changed later.
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    // Get all users and convert each one to a response
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    // Get one user by id. Throws 404 if it does not exist.
    public UserResponse findById(Long id) {
        return userMapper.toResponse(getUserOrThrow(id));
    }

    // Search by name or email. If the text is empty, return all users.
    public List<UserResponse> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String term = keyword.trim();   // Remove extra spaces
        return userRepository.findByNameContainingOrEmailContaining(term, term).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    // Create a new user.
    // @Transactional (without readOnly) allows writing to the database.
    @Transactional
    public UserResponse create(UserRequest request) {
        // Step 1: the email must not be used already
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new DuplicateResourceException(
                    "A user with email '" + request.getEmail() + "' already exists");
        }
        // Step 2: the role must exist
        Role role = getRoleOrThrow(request.getRoleId());

        // Step 3: save the user and return it as a response
        User saved = userRepository.save(userMapper.toEntity(request, role));
        return userMapper.toResponse(saved);
    }

    // Update an existing user
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        // Step 1: the user must exist
        User user = getUserOrThrow(id);

        // Step 2: no OTHER user may have this email (the user can keep their own email)
        if (userRepository.existsByEmailAndIdNot(request.getEmail().trim(), id)) {
            throw new DuplicateResourceException(
                    "A user with email '" + request.getEmail() + "' already exists");
        }
        // Step 3: the role must exist
        Role role = getRoleOrThrow(request.getRoleId());

        // Step 4: copy the new values into the user and save.
        // saveAndFlush writes now, so @PreUpdate sets updatedAt before we build the response.
        userMapper.updateEntity(user, request, role);
        return userMapper.toResponse(userRepository.saveAndFlush(user));   // [CHANGED] save -> saveAndFlush
    }

    // Delete a user. Throws 404 if it does not exist.
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }

    // Helper: find a user or throw "not found" (the handler turns it into 404)
    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    // Helper: find a role or throw "not found".
    // Inactive roles cannot be given to users (the error is shown under the role field).
    private Role getRoleOrThrow(Long roleId) {
        // [CHANGED] the role is stored in a variable (it was returned directly before)
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + roleId));
        // [ADDED] inactive roles cannot be assigned
        if (role.getStatus() == Status.INACTIVE) {
            throw new InvalidOperationException("roleId",
                    "Role " + role.getName() + " is inactive and cannot be assigned");
        }
        return role;
    }
}