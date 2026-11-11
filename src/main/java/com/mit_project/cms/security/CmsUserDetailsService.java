// [NEW FILE - login] Added in the Login update.
package com.mit_project.cms.security;

import com.mit_project.cms.model.Status;
import com.mit_project.cms.model.User;
import com.mit_project.cms.repository.UserRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security calls this when someone tries to log in.
 * It loads the user by email from our database and turns them into a CmsUserDetails.
 * Spring Security then compares the typed password with the saved hash itself.
 */
@Service
public class CmsUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CmsUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new UsernameNotFoundException("No user with this email"));

        // Users without a password (older users) cannot log in yet
        if (user.getPasswordHash() == null) {
            throw new UsernameNotFoundException("This user has no password yet");
        }

        // The role becomes an "authority": ADMIN -> ROLE_ADMIN. SecurityConfig checks these.
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getName());

        // enabled = false for INACTIVE users (checked AFTER the password, see SecurityConfig)
        boolean enabled = user.getStatus() == Status.ACTIVE;

        return new CmsUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(),
                enabled, List.of(authority));
    }
}
