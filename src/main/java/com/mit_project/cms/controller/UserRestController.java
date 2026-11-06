package com.mit_project.cms.controller;

import com.mit_project.cms.dto.UserRequest;
import com.mit_project.cms.dto.UserResponse;
import com.mit_project.cms.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST API for users. It returns JSON.
 * The controller stays thin: it receives the request and
 * passes the work to UserService.
 */
@RestController               // Returns JSON instead of HTML pages
@RequestMapping("/api/users") // All URLs in this class start with /api/users
public class UserRestController {

    private final UserService userService;

    // Spring passes in UserService (constructor injection)
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users            -> all users
    // GET /api/users?search=an  -> users matching "an" in name or email
    @GetMapping
    public List<UserResponse> getAll(@RequestParam(required = false) String search) {
        return userService.search(search);
    }

    // GET /api/users/1 -> one user. {id} is read from the URL.
    // If the user does not exist, the service throws an exception (404).
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    // POST /api/users -> create a user
    // @RequestBody: turns the JSON into a UserRequest
    // @Valid: checks the validation rules (name, email, role). Bad data gives 400.
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.create(request);

        // Build the URL of the new user, for example /api/users/6
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        // Status 201 Created, with the Location header and the new user in the body
        return ResponseEntity.created(location).body(created);
    }

    // PUT /api/users/1 -> update a user. The data is validated the same way.
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return userService.update(id, request);
    }

    // DELETE /api/users/1 -> delete a user
    // Returns status 204 No Content (success, nothing to send back)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}