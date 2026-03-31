package com;

import com.Security.PasswordResetTokenException;
import com.Security.RefreshTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

/**
 * Global Exception Handler for consistent error responses.
 * Handles all application exceptions and returns standardized API errors.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ==========================================================
    // 404 - NOT FOUND
    // ==========================================================
    @ExceptionHandler({
            // USER
            com.User.UserNotFoundException.class,

            // ROLE
            com.Role.RoleNotFoundException.class,

            // ROOM
            com.Room.RoomNotFoundException.class,

            // ROOM TYPE
            com.RoomType.RoomTypeNotFoundException.class,

            // HOTEL
            com.Hotel.HotelNotFoundException.class,

            // AMENITY
            com.Amenity.AmenityNotFoundException.class,

            // BOOKING
            com.Booking.BookingNotFoundException.class,

            // PAYMENT
            com.Payment.PaymentNotFoundException.class,

            // IMAGE
            com.Image.ImageNotFoundException.class,

            // REVIEW
            com.Review.ReviewNotFoundException.class,

            // ROOM AVAILABILITY
            com.RoomAvailability.RoomAvailabilityNotFoundException.class,

            // BOOKING GUEST
            com.BookingGuest.BookingGuestNotFoundException.class,

            // NOTIFICATION
            com.Notification.NotificationNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex,
                                                   HttpServletRequest request) {

        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    // ==========================================================
    // 409 - CONFLICT (Already Exists)
    // ==========================================================
    @ExceptionHandler({
            com.User.UserAlreadyExistsException.class,
            com.Role.RoleAlreadyExistsException.class,
            com.Room.RoomAlreadyExistsException.class,
            com.RoomType.RoomTypeAlreadyExistsException.class,
            com.Hotel.HotelAlreadyExistsException.class,
            com.Amenity.AmenityAlreadyExistsException.class,
            com.Booking.BookingAlreadyExistsException.class,
            com.Payment.PaymentAlreadyExistsException.class,
            com.Review.ReviewAlreadyExistsException.class,
            com.RoomAvailability.RoomAvailabilityAlreadyExistsException.class
    })
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex,
                                                   HttpServletRequest request) {

        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    // ==========================================================
    // 400 - BAD REQUEST (Entity-specific)
    // ==========================================================
    @ExceptionHandler({
            com.User.UserBadRequestException.class,
            com.Role.RoleBadRequestException.class,
            com.Room.RoomBadRequestException.class,
            com.RoomType.RoomTypeBadRequestException.class,
            com.Booking.BookingBadRequestException.class,
            com.Payment.PaymentBadRequestException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex,
                                                     HttpServletRequest request) {

        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    // ==========================================================
    // 409 - DATABASE CONFLICT
    // ==========================================================
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDatabaseConflict(DataIntegrityViolationException ex,
                                                           HttpServletRequest request) {

        log.warn("Database integrity violation at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse("Database integrity violation", HttpStatus.CONFLICT, request);
    }

    // ==========================================================
    // 400 - BAD REQUEST (IllegalArgumentException)
    // ==========================================================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex,
                                                          HttpServletRequest request) {

        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    // ==========================================================
    // 400 - VALIDATION (@Valid body)
    // ==========================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationError(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return buildResponse(message, HttpStatus.BAD_REQUEST, request);
    }

    // ==========================================================
    // 400 - VALIDATION (Request params / path vars)
    // ==========================================================
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
                                                              HttpServletRequest request) {

        String message = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .orElse("Invalid request parameter");

        return buildResponse(message, HttpStatus.BAD_REQUEST, request);
    }

    // ==========================================================
    // 400 - MALFORMED JSON
    // ==========================================================
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonError(HttpMessageNotReadableException ex,
                                                    HttpServletRequest request) {

        log.debug("Malformed JSON at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse("Invalid request body format", HttpStatus.BAD_REQUEST, request);
    }

    // ==========================================================
    // 400 - PARAMETER TYPE MISMATCH
    // ==========================================================
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                       HttpServletRequest request) {

        String message = "Invalid value for parameter '" + ex.getName() + "'";
        return buildResponse(message, HttpStatus.BAD_REQUEST, request);
    }

    // ==========================================================
    // 401 - UNAUTHORIZED
    // ==========================================================
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex,
                                                         HttpServletRequest request) {

        log.warn("Authentication failed at {}: {}", request.getRequestURI(), ex.getMessage());

        return buildResponse("Invalid username or password",
                HttpStatus.UNAUTHORIZED, request);
    }

    // ==========================================================
    // 403 - FORBIDDEN (Refresh Token)
    // ==========================================================
    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ApiError> handleRefreshTokenError(RefreshTokenException ex,
                                                            HttpServletRequest request) {

        log.warn("Refresh token error at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request);
    }

    // ==========================================================
    // 400 - BAD REQUEST (Password Reset Token)
    // ==========================================================
    @ExceptionHandler(PasswordResetTokenException.class)
    public ResponseEntity<ApiError> handlePasswordResetTokenError(PasswordResetTokenException ex,
                                                                  HttpServletRequest request) {

        log.warn("Password reset token error at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    // ==========================================================
    // 403 - FORBIDDEN (Access Denied)
    // ==========================================================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex,
                                                       HttpServletRequest request) {

        log.warn("Access denied at {}: {}", request.getRequestURI(), ex.getMessage());

        return buildResponse("You do not have permission to access this resource",
                HttpStatus.FORBIDDEN, request);
    }

    // ==========================================================
    // 423 - LOCKED (Account Locked)
    // ==========================================================
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiError> handleAccountLocked(LockedException ex,
                                                        HttpServletRequest request) {

        log.warn("Account locked at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.LOCKED, request);
    }

    // ==========================================================
    // 500 - FALLBACK
    // ==========================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex,
                                                          HttpServletRequest request) {

        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildResponse("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // ==========================================================
    // Helper method
    // ==========================================================
    private ResponseEntity<ApiError> buildResponse(String message,
                                                   HttpStatus status,
                                                   HttpServletRequest request) {

        ApiError body = new ApiError(
                Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(body);
    }
}
