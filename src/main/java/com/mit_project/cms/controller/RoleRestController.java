// [NEW FILE] Added in the Status / GUEST update.
package com.mit_project.cms.controller;

import com.mit_project.cms.dto.RoleResponse;
import com.mit_project.cms.dto.RoleStatusRequest;
import com.mit_project.cms.service.RoleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for roles. It returns JSON.
 * Roles are fixed (ADMIN, MANAGER, USER, GUEST); only their status can change.
 */
@RestController
@RequestMapping("/api/roles")
public class RoleRestController {

    private final RoleService roleService;

    public RoleRestController(RoleService roleService) {
        this.roleService = roleService;
    }

    // GET /api/roles -> all roles with their status and user count
    @GetMapping
    public List<RoleResponse> getAll() {
        return roleService.findAll();
    }

    // PATCH /api/roles/2/status  body: {"status": "INACTIVE"}
    // Deactivating a role moves its users to GUEST. GUEST cannot be deactivated (400).
    @PatchMapping("/{id}/status")
    public RoleResponse updateStatus(@PathVariable Long id,
                                     @Valid @RequestBody RoleStatusRequest request) {
        return roleService.updateStatus(id, request.status());
    }
}
