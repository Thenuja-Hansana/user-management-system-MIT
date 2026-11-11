// [NEW FILE] Added in the Status / GUEST update.
package com.mit_project.cms.dto;

import com.mit_project.cms.model.Status;
import java.time.LocalDateTime;

/**
 * Data going OUT to the client when we return a role.
 * A record is a short way to write a read-only data class.
 *
 * @param userCount how many users currently have this role
 */
public record RoleResponse(
        Long id,
        String name,
        Status status,
        LocalDateTime updatedAt,
        long userCount) {
}
