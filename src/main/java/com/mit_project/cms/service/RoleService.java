// [NEW FILE] Added in the Status / GUEST update.
package com.mit_project.cms.service;

import com.mit_project.cms.dto.RoleResponse;
import com.mit_project.cms.exception.InvalidOperationException;
import com.mit_project.cms.exception.ResourceNotFoundException;
import com.mit_project.cms.model.Role;
import com.mit_project.cms.model.Status;
import com.mit_project.cms.repository.RoleRepository;
import com.mit_project.cms.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The business logic for roles.
 *
 * Main rule: when a role becomes INACTIVE, every user with that role
 * is moved to the GUEST role. GUEST itself can never be made inactive.
 * When a role is made active again, nobody is moved back automatically.
 */
@Service
@Transactional(readOnly = true)  // By default, every method runs in a read-only transaction
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    // All roles, ordered by id, with the number of users in each
    public List<RoleResponse> findAll() {
        return roleRepository.findAll(Sort.by("id")).stream().map(this::toResponse).toList();
    }

    // Change a role's status (ACTIVE / INACTIVE)
    @Transactional
    public RoleResponse updateStatus(Long id, Status status) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + id));

        // Nothing to do if the status is already the same
        if (role.getStatus() == status) {
            return toResponse(role);
        }

        // GUEST is the fallback role, so it must always stay active
        // [CHANGED - login] ADMIN must always stay active too, so admins can never be locked out
        if ((role.isGuest() || role.isAdmin()) && status == Status.INACTIVE) {
            throw new InvalidOperationException("The " + role.getName() + " role cannot be deactivated");
        }

        role.setStatus(status);
        // saveAndFlush writes now, so @PreUpdate sets updatedAt before we build the response
        roleRepository.saveAndFlush(role);

        // Role is now inactive: move all its users to GUEST
        if (status == Status.INACTIVE) {
            Role guest = roleRepository.findByName(Role.GUEST)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with name " + Role.GUEST));
            userRepository.reassignRole(role, guest, LocalDateTime.now());
        }

        return toResponse(role);
    }

    private RoleResponse toResponse(Role role) {
        return new RoleResponse(role.getId(), role.getName(), role.getStatus(),
                role.getUpdatedAt(), userRepository.countByRole(role));
    }
}
