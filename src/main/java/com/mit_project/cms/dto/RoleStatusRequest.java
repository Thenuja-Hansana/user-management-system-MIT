// [NEW FILE] Added in the Status / GUEST update.
package com.mit_project.cms.dto;

import com.mit_project.cms.model.Status;
import jakarta.validation.constraints.NotNull;

/**
 * Body of PATCH /api/roles/{id}/status, for example {"status": "INACTIVE"}.
 */
public record RoleStatusRequest(
        @NotNull(message = "Status is required") Status status) {
}
