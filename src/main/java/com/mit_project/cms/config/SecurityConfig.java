// [NEW FILE - login] Added in the Login update.
package com.mit_project.cms.config;

import com.mit_project.cms.security.CmsUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

/**
 * All the login and permission rules of the application.
 *
 * Who can do what:
 *   ADMIN    - everything (users: add, edit, delete; roles: activate / deactivate)
 *   MANAGER  - see all users and roles, delete users (but not Admins and not themselves)
 *   USER     - only "My Account" (their own details, read-only)
 *   GUEST    - only "My Account" (their own details, read-only)
 *
 * How login works:
 *   1. login.html sends the email + password to POST /login.
 *   2. Spring Security checks them (CmsUserDetailsService + the password hash).
 *   3. If they are right, the server creates a SESSION and the browser gets a session cookie (JSESSIONID).
 *      The browser sends this cookie with every request, so the server knows who you are.
 *   4. POST /logout ends the session.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CmsUserDetailsService userDetailsService,
                                                   PasswordEncoder passwordEncoder) throws Exception {
        http
            // Which URLs need which role. The FIRST rule that matches a request wins, so the order matters.
            .authorizeHttpRequests(auth -> auth
                // Open to everyone (needed before logging in)
                .requestMatchers("/login.html", "/js/**", "/error", "/favicon.ico").permitAll()
                // Any logged-in user can see their own details
                .requestMatchers("/api/me", "/account.html").authenticated()
                // Admin + Manager: see users and roles
                .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/**", "/api/roles").hasAnyRole("ADMIN", "MANAGER")
                // Admin + Manager: delete users (UserService adds: managers cannot delete Admins or themselves)
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyRole("ADMIN", "MANAGER")
                // Everything else in the API (add/edit users, change roles): Admin only
                .requestMatchers("/api/**").hasRole("ADMIN")
                // The CMS page itself: Admin + Manager
                .requestMatchers("/", "/index.html").hasAnyRole("ADMIN", "MANAGER")
                // Anything else: must be logged in
                .anyRequest().authenticated())

            // Login with our own page (login.html). It sends "email" and "password" to POST /login.
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(this::onLoginSuccess)
                .failureHandler(this::onLoginFailure)
                .permitAll())

            // POST /logout ends the session and removes the session cookie
            .logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) ->
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT)))

            .exceptionHandling(ex -> ex
                // Not logged in + API request -> 401 JSON (a page request goes to login.html instead)
                .defaultAuthenticationEntryPointFor(
                        (request, response, e) -> writeJson(response, 401,
                                "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Please log in\"}"),
                        PathPatternRequestMatcher.pathPattern("/api/**"))
                // Logged in but not allowed
                .accessDeniedHandler(this::onAccessDenied))

            // CSRF protection for a JavaScript front end: the server puts a token in the XSRF-TOKEN cookie,
            // and our pages send it back in the X-XSRF-TOKEN header on every POST / PUT / PATCH / DELETE.
            // Another website cannot read our cookie, so it cannot send the right token.
            .csrf(csrf -> csrf.spa())

            // How to check email + password (see the method below)
            .authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder));

        return http.build();
    }

    // Checks email + password. We change one thing from the default:
    // the password is checked FIRST and only then "is the account active?".
    // So "account is inactive" is only shown to someone who typed the right password.
    private DaoAuthenticationProvider authenticationProvider(CmsUserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setPreAuthenticationChecks(user -> { });   // nothing before the password check
        provider.setPostAuthenticationChecks(user -> {      // after the password check
            if (!user.isEnabled()) {
                throw new DisabledException("Account is inactive");
            }
        });
        return provider;
    }

    // Login OK -> tell the page where to go: Admin/Manager to the CMS, User/Guest to My Account
    private void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        boolean canUseCms = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));
        String page = canUseCms ? "/index.html" : "/account.html";
        writeJson(response, 200, "{\"redirect\":\"" + page + "\"}");
    }

    // Login failed -> 401 with a message for the login page
    private void onLoginFailure(HttpServletRequest request, HttpServletResponse response,
                                AuthenticationException exception) throws IOException {
        String message = (exception instanceof DisabledException)
                ? "Your account is inactive. Please contact an administrator."
                : "Invalid email or password";
        writeJson(response, 401, "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}");
    }

    // Logged in but not allowed:
    //  - opening a page you may not see (e.g. a USER opens the CMS) -> go to My Account
    //  - an API call you may not make -> 403 JSON
    private void onAccessDenied(HttpServletRequest request, HttpServletResponse response,
                                AccessDeniedException exception) throws IOException {
        boolean isPageRequest = "GET".equals(request.getMethod()) && !request.getRequestURI().startsWith("/api/");
        if (isPageRequest) {
            response.sendRedirect("/account.html");
            return;
        }
        String message = (exception instanceof CsrfException)
                ? "Your security token is missing or expired. Please reload the page and try again."
                : "You do not have permission to do this";
        writeJson(response, 403, "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"" + message + "\"}");
    }

    // Helper: send a small JSON answer. (The messages are fixed texts, never user input.)
    private static void writeJson(HttpServletResponse response, int status, String json) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
