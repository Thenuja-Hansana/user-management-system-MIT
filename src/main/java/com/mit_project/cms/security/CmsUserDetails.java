// [NEW FILE - login] Added in the Login update.
package com.mit_project.cms.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * The logged-in user, as Spring Security sees them.
 *
 * Spring Security's own User class already holds the username (we use the email),
 * the password hash, "is the account enabled?" and the roles (authorities).
 * We extend it to also remember the user's database id, so we can always find
 * "who is logged in" even if they change their email later.
 *
 * (Not to be confused with com.mit_project.cms.model.User, our database entity.)
 */
public class CmsUserDetails extends User {

    private final Long id;   // id of the user in our users table

    public CmsUserDetails(Long id, String email, String passwordHash, boolean enabled,
                          Collection<? extends GrantedAuthority> authorities) {
        // The three "true" values: account not expired, password not expired, account not locked
        super(email, passwordHash, enabled, true, true, true, authorities);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    // true if the logged-in user has the ADMIN role
    public boolean isAdmin() {
        return getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
