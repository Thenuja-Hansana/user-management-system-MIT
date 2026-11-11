// [NEW FILE - login] Added in the Login update.
package com.mit_project.cms.controller;

import com.mit_project.cms.dto.UserResponse;
import com.mit_project.cms.security.CmsUserDetails;
import com.mit_project.cms.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * "Who am I?" for the logged-in user. Every role may use it.
 * The pages use it to show the user's name and to decide what to show.
 */
@RestController
@RequestMapping("/api/me")
public class AccountRestController {

    private final UserService userService;

    public AccountRestController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/me -> the logged-in user's own details
    // @AuthenticationPrincipal: Spring passes in the logged-in user (from the session)
    @GetMapping
    public UserResponse me(@AuthenticationPrincipal CmsUserDetails currentUser) {
        return userService.findById(currentUser.getId());
    }
}
