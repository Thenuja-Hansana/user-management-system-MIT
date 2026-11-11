// [NEW FILE] Added in the Status / GUEST update.
package com.mit_project.cms.exception;

/**
 * The request is well-formed but breaks a business rule,
 * for example giving a user an inactive role or deactivating GUEST.
 *
 * If a field name is given, the error is shown under that field in the form.
 */
public class InvalidOperationException extends RuntimeException {

    private final String field;   // Form field the error belongs to, or null

    public InvalidOperationException(String message) {
        this(null, message);
    }

    public InvalidOperationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
