package com.Security;

import com.Role.Role;
import com.Role.RoleRepository;
import com.User.User;
import com.User.UserAlreadyExistsException;
import com.User.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Authentication Controller handling login, registration, token refresh, and logout.
 * Implements JWT-based authentication with refresh token support.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${app.security.lockout-duration:900000}")
    private long lockoutDuration; // 15 minutes

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          RefreshTokenService refreshTokenService,
                          PasswordResetTokenService passwordResetTokenService,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate user and return JWT tokens
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT access and refresh tokens")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            // Check if account is locked
            Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.isAccountLocked()) {
                    if (user.getLockTime() != null &&
                            user.getLockTime().plusSeconds(lockoutDuration / 1000).isBefore(LocalDateTime.now())) {
                        // Unlock account after lockout duration
                        user.setAccountLocked(false);
                        user.setFailedLoginAttempts(0);
                        user.setLockTime(null);
                        userRepository.save(user);
                    } else {
                        throw new LockedException("Account is locked. Please try again later.");
                    }
                }
            }

            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Reset failed attempts on successful login
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.getFailedLoginAttempts() > 0) {
                    user.setFailedLoginAttempts(0);
                    userRepository.save(user);
                }
            }

            // Generate tokens
            String accessToken = jwtTokenProvider.generateToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequest.getUsername());

            User user = userOpt.get();
            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            LoginResponseDTO response = LoginResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .expiresIn(jwtTokenProvider.getJwtExpirationMs())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(roles)
                    .build();

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            // Increment failed attempts
            Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                int attempts = user.getFailedLoginAttempts() + 1;
                user.setFailedLoginAttempts(attempts);

                if (attempts >= maxFailedAttempts) {
                    user.setAccountLocked(true);
                    user.setLockTime(LocalDateTime.now());
                }
                userRepository.save(user);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MessageResponseDTO.error("Invalid username or password"));

        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(MessageResponseDTO.error(e.getMessage()));
        }
    }

    /**
     * Register a new user
     */
    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register a new user account")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        // Check password match
        if (!registerRequest.isPasswordMatching()) {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDTO.error("Passwords do not match"));
        }

        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + registerRequest.getUsername());
        }

        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + registerRequest.getEmail());
        }

        // Validate password complexity
        if (!isPasswordValid(registerRequest.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDTO.error(
                            "Password must contain at least 8 characters, including uppercase, lowercase, number, and special character"));
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);

        // Assign default USER role
        Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
        if (userRole.isPresent()) {
            user.setRoles(new HashSet<>(Collections.singletonList(userRole.get())));
        }

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MessageResponseDTO.success("User registered successfully"));
    }

    /**
     * Refresh access token using refresh token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        Optional<RefreshToken> tokenOpt = refreshTokenService.findByToken(request.getRefreshToken());

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(MessageResponseDTO.error("Invalid refresh token"));
        }

        RefreshToken refreshToken = refreshTokenService.verifyExpiration(tokenOpt.get());
        User user = refreshToken.getUser();

        String roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));

        String accessToken = jwtTokenProvider.generateTokenFromUsername(user.getUsername(), roles);

        LoginResponseDTO response = LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken())
                .expiresIn(jwtTokenProvider.getJwtExpirationMs())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Logout user by revoking refresh token
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout user and invalidate refresh token")
    public ResponseEntity<?> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            refreshTokenService.revokeToken(request.getRefreshToken());
            return ResponseEntity.ok(MessageResponseDTO.success("Logged out successfully"));
        } catch (RefreshTokenException e) {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDTO.error(e.getMessage()));
        }
    }

    /**
     * Get current authenticated user info
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user information")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MessageResponseDTO.error("Not authenticated"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());
        response.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        // Get additional user info
        userRepository.findByUsername(authentication.getName()).ifPresent(user -> {
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
        });

        return ResponseEntity.ok(response);
    }

    /**
     * Change password for authenticated user
     */
    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for authenticated user")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request,
                                            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MessageResponseDTO.error("Not authenticated"));
        }

        // Check password match
        if (!request.isPasswordMatching()) {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDTO.error("New passwords do not match"));
        }

        // Validate password complexity
        if (!isPasswordValid(request.getNewPassword())) {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDTO.error(
                            "Password must contain at least 8 characters, including uppercase, lowercase, number, and special character"));
        }

        // Get current user
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDTO.error("Current password is incorrect"));
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Revoke all refresh tokens for security
        refreshTokenService.revokeAllUserTokens(user.getId());

        return ResponseEntity.ok(MessageResponseDTO.success("Password changed successfully"));
    }

    /**
     * Request password reset token
     * Returns token directly (in production, this would be sent via email)
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Request a password reset token")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        PasswordResetToken token = passwordResetTokenService.createResetToken(request.getEmail());

        // For security, always return success even if email doesn't exist
        if (token == null) {
            return ResponseEntity.ok(MessageResponseDTO.success(
                    "If the email exists, a reset token has been generated"));
        }

        // In production, token would be emailed. For now, return it in response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password reset token generated successfully");
        response.put("token", token.getToken());
        response.put("expiresAt", token.getExpiryDate().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Reset password using token
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using reset token")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        // Check password match
        if (!request.isPasswordMatching()) {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDTO.error("Passwords do not match"));
        }

        // Validate password complexity
        if (!isPasswordValid(request.getNewPassword())) {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDTO.error(
                            "Password must contain at least 8 characters, including uppercase, lowercase, number, and special character"));
        }

        try {
            // Validate token and get user
            User user = passwordResetTokenService.validateAndConsumeToken(request.getToken());

            // Update password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            // Revoke all refresh tokens for security
            refreshTokenService.revokeAllUserTokens(user.getId());

            return ResponseEntity.ok(MessageResponseDTO.success("Password reset successfully"));

        } catch (PasswordResetTokenException e) {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDTO.error(e.getMessage()));
        }
    }

    /**
     * Validate password complexity
     */
    private boolean isPasswordValid(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }
}
