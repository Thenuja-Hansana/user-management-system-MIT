// [NEW FILE] Added in the Status / GUEST update.
package com.mit_project.cms.model;

/**
 * Status of a role or a user.
 * Stored in the database as text ("ACTIVE" / "INACTIVE"), not as a number,
 * so the data stays readable and adding values later does not break old rows.
 */
public enum Status {
    ACTIVE,
    INACTIVE
}
