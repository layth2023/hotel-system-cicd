package com.Review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Review management.
 */
@RestController
@RequestMapping("/reviews")
@Tag(name = "Reviews", description = "Review management endpoints")
public class ReviewController {

    private final ReviewService reviewService;
    private final com.User.UserRepository userRepository;

    public ReviewController(ReviewService reviewService, com.User.UserRepository userRepository) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create review", description = "Create a new review for a hotel")
    public ResponseEntity<ReviewResponseDTO> create(
            @Valid @RequestBody ReviewRequestDTO dto,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        ReviewResponseDTO created = reviewService.create(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID", description = "Retrieve a review by its ID")
    public ReviewResponseDTO getById(@PathVariable Long id) {
        return reviewService.getById(id);
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get reviews by hotel", description = "Retrieve all approved reviews for a hotel")
    public Page<ReviewResponseDTO> getByHotel(
            @PathVariable Long hotelId,
            Pageable pageable) {
        return reviewService.getByHotel(hotelId, pageable);
    }

    @GetMapping("/my-reviews")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my reviews", description = "Retrieve all reviews by the current user")
    public List<ReviewResponseDTO> getMyReviews(Authentication authentication) {
        Long userId = getUserId(authentication);
        return reviewService.getByUser(userId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update review", description = "Update an existing review")
    public ReviewResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO dto,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        return reviewService.update(id, dto, userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete review", description = "Delete a review")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        reviewService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Approve review", description = "Approve a pending review (Admin/Manager only)")
    public ReviewResponseDTO approve(@PathVariable Long id) {
        return reviewService.approve(id);
    }

    @PostMapping("/{id}/response")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Add response", description = "Add hotel response to a review (Admin/Manager only)")
    public ReviewResponseDTO addResponse(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String response = request.get("response");
        return reviewService.addResponse(id, response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get pending reviews", description = "Get all pending reviews (Admin/Manager only)")
    public Page<ReviewResponseDTO> getPendingReviews(Pageable pageable) {
        return reviewService.getPendingReviews(pageable);
    }

    @GetMapping("/hotel/{hotelId}/rating")
    @Operation(summary = "Get average rating", description = "Get average rating for a hotel")
    public Map<String, Object> getAverageRating(@PathVariable Long hotelId) {
        Double rating = reviewService.getAverageRating(hotelId);
        return Map.of("hotelId", hotelId, "averageRating", rating != null ? rating : 0.0);
    }

    private Long getUserId(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
