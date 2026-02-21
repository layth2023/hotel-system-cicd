package com.User;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }


    // GET /users?page=0&size=10&sort=id,desc
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable));
    }

    // GET /users/{id}
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    // POST /users
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    // PUT /users/{id}
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO dto,
            Authentication authentication
    ) {
        // Self-check: if not admin, user must update himself only
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            String username = authentication.getName();
            User me = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

            if (!me.getId().equals(id)) {
                throw new UserBadRequestException("You can only update your own account");
            }
        }

        return ResponseEntity.ok(userService.update(id, dto));
    }

    // DELETE /users/{id}
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /users/{id}/enabled?value=true
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/enabled")
    public ResponseEntity<UserResponseDTO> setEnabled(@PathVariable Long id,
                                                      @RequestParam boolean value) {
        return ResponseEntity.ok(userService.setEnabled(id, value));
    }

    // POST /users/{userId}/roles/{roleId}
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserResponseDTO> addRole(@PathVariable Long userId,
                                                   @PathVariable Long roleId) {
        return ResponseEntity.ok(userService.addRoleToUser(userId, roleId));
    }

    // DELETE /users/{userId}/roles/{roleId}
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserResponseDTO> removeRole(@PathVariable Long userId,
                                                      @PathVariable Long roleId) {
        return ResponseEntity.ok(userService.removeRoleFromUser(userId, roleId));
    }
}