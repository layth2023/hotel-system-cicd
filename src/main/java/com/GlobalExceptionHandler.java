package com.Exception;

import com.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

/**
 * Global Exception Handler
 * This class handles all exceptions across the whole application.
 * It replaces all entity-specific exception handlers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==========================================================
    // 🔴 404 - NOT FOUND
    // ==========================================================
    @ExceptionHandler({
            // USER
            com.User.UserNotFoundException.class,

            // ROLE
            com.Role.RoleNotFoundException.class,

            // ROOM
            com.Room.RoomNotFoundException.class,

            // ROOM TYPE
            com.RoomType.RoomTypeNotFoundException.class

            // ✅ Add more NotFound exceptions here when you create new modules
    })
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex, HttpServletRequest request) {

        ApiError body = new ApiError(
                Instant.now().toString(),
                404,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // ==========================================================
    // 🟠 409 - CONFLICT (Already Exists + DB conflicts)
    // ==========================================================
    @ExceptionHandler({
            // USER
            com.User.UserAlreadyExistsException.class,

            // ROLE
            com.Role.RoleAlreadyExistsException.class,

            // ROOM
            com.Room.RoomAlreadyExistsException.class,

            // ROOM TYPE
            com.RoomType.RoomTypeAlreadyExistsException.class

            // ✅ Add more AlreadyExists exceptions here
    })
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex, HttpServletRequest request) {

        ApiError body = new ApiError(
                Instant.now().toString(),
                409,
                "Conflict",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // ✅ database unique constraints / FK constraints, etc.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDatabaseConflict(DataIntegrityViolationException ex,
                                                           HttpServletRequest request) {

        ApiError body = new ApiError(
                Instant.now().toString(),
                409,
                "Database Conflict",
                "Data integrity violation",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // ==========================================================
    // 🟡 400 - BAD REQUEST
    // ==========================================================
    @ExceptionHandler({
            // USER
            com.User.UserBadRequestException.class,

            // ROLE
            com.Role.RoleBadRequestException.class,

            // ROOM
            com.Room.RoomBadRequestException.class,

            // ROOM TYPE
            com.RoomType.RoomTypeBadRequestException.class

            // ✅ Add more BadRequest exceptions here
    })
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex, HttpServletRequest request) {

        ApiError body = new ApiError(
                Instant.now().toString(),
                400,
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ==========================================================
    // 🟡 400 - VALIDATION (@Valid)
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

        ApiError body = new ApiError(
                Instant.now().toString(),
                400,
                "Validation Error",
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ==========================================================
    // 🟡 400 - MALFORMED JSON
    // ==========================================================
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonError(HttpMessageNotReadableException ex,
                                                    HttpServletRequest request) {

        ApiError body = new ApiError(
                Instant.now().toString(),
                400,
                "Malformed JSON",
                "Invalid request body format",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ==========================================================
    // 🟡 400 - PARAMETER TYPE MISMATCH
    // ==========================================================
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                       HttpServletRequest request) {

        ApiError body = new ApiError(
                Instant.now().toString(),
                400,
                "Invalid Parameter",
                "Invalid request parameter type",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ==========================================================
    // 🔵 401 - UNAUTHORIZED (wrong credentials)
    // ==========================================================
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex,
                                                         HttpServletRequest request) {

        ApiError body = new ApiError(
                Instant.now().toString(),
                401,
                "Unauthorized",
                "Invalid username or password",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // ==========================================================
    // 🟣 403 - FORBIDDEN
    // ==========================================================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex,
                                                       HttpServletRequest request) {

        ApiError body = new ApiError(
                Instant.now().toString(),
                403,
                "Forbidden",
                "You do not have permission to access this resource",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // ==========================================================
    // ⚫ 500 - INTERNAL SERVER ERROR (Fallback)
    // ==========================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex,
                                                          HttpServletRequest request) {

        ApiError body = new ApiError(
                Instant.now().toString(),
                500,
                "Internal Server Error",
                "Something went wrong",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}