package com.mit_project.cms.service;

import com.mit_project.cms.dto.UserRequest;
import com.mit_project.cms.dto.UserResponse;
import com.mit_project.cms.exception.DuplicateResourceException;
import com.mit_project.cms.exception.ResourceNotFoundException;
import com.mit_project.cms.mapper.UserMapper;
import com.mit_project.cms.model.Role;
import com.mit_project.cms.model.User;
import com.mit_project.cms.repository.RoleRepository;
import com.mit_project.cms.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    public UserResponse findById(Long id) {
        return userMapper.toResponse(getUserOrThrow(id));
    }

    public List<UserResponse> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String term = keyword.trim();
        return userRepository.findByNameContainingOrEmailContaining(term, term).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new DuplicateResourceException(
                    "A user with email '" + request.getEmail() + "' already exists");
        }
        Role role = getRoleOrThrow(request.getRoleId());
        User saved = userRepository.save(userMapper.toEntity(request, role));
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = getUserOrThrow(id);
        if (userRepository.existsByEmailAndIdNot(request.getEmail().trim(), id)) {
            throw new DuplicateResourceException(
                    "A user with email '" + request.getEmail() + "' already exists");
        }
        Role role = getRoleOrThrow(request.getRoleId());
        userMapper.updateEntity(user, request, role);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    private Role getRoleOrThrow(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + roleId));
    }
}