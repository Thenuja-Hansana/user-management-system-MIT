// [NEW FILE - password] Added in the Password update.
package com.mit_project.cms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Creates the password "hasher" used by UserService.
 *
 * We never save the real password. BCrypt turns it into a scrambled text (a hash)
 * that cannot be turned back into the password. To check a password later
 * (for example at login) you call passwordEncoder.matches(typedPassword, savedHash).
 */
@Configuration   // This class gives Spring objects (beans) to share
public class PasswordConfig {

    // @Bean: Spring creates this object once and passes it to anyone who asks for a PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
