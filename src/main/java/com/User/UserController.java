package com.User;

import com.Booking.BookingMapper;
import com.Booking.BookingRepository;
import com.Booking.BookingResponseDTO;
import com.Payment.PaymentMapper;
import com.Payment.PaymentRepository;
import com.Payment.PaymentResponseDTO;
import com.Review.ReviewMapper;
import com.Review.ReviewRepository;
import com.Review.ReviewResponseDTO;
import com.Security.CustomUserDetails;
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
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;
    private final BookingMapper bookingMapper;
    private final ReviewMapper reviewMapper;

    public UserController(UserService userService,
                          UserRepository userRepository,
                          BookingRepository bookingRepository,
                          ReviewRepository reviewRepository,
                          PaymentRepository paymentRepository,
                          BookingMapper bookingMapper,
                          ReviewMapper reviewMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
        this.paymentRepository = paymentRepository;
        this.bookingMapper = bookingMapper;
        this.reviewMapper = reviewMapper;
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

    // GET /users/me - Get current user profile
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(userService.getById(userId));
    }

    // GET /users/me/bookings - Get current user's bookings
    @GetMapping("/me/bookings")
    public ResponseEntity<Page<BookingResponseDTO>> getMyBookings(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        Long userId = getCurrentUserId(authentication);
        Page<BookingResponseDTO> bookings = bookingRepository.findByUserId(userId, pageable)
                .map(bookingMapper::toResponseDTO);
        return ResponseEntity.ok(bookings);
    }

    // GET /users/me/reviews - Get current user's reviews
    @GetMapping("/me/reviews")
    public ResponseEntity<Page<ReviewResponseDTO>> getMyReviews(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        Long userId = getCurrentUserId(authentication);
        Page<ReviewResponseDTO> reviews = reviewRepository.findByUserId(userId, pageable)
                .map(reviewMapper::toResponseDTO);
        return ResponseEntity.ok(reviews);
    }

    // GET /users/me/payments - Get current user's payments
    @GetMapping("/me/payments")
    public ResponseEntity<Page<PaymentResponseDTO>> getMyPayments(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        Long userId = getCurrentUserId(authentication);
        Page<PaymentResponseDTO> payments = paymentRepository.findByUserId(userId, pageable)
                .map(PaymentMapper::toDTO);
        return ResponseEntity.ok(payments);
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null) {
            throw new UserNotFoundException("Not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getId();
        }

        // Fallback to username lookup
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        return user.getId();
    }
}