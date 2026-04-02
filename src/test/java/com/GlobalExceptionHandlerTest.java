package com;

import com.Booking.BookingBadRequestException;
import com.Booking.BookingNotFoundException;
import com.Hotel.HotelAlreadyExistsException;
import com.Hotel.HotelNotFoundException;
import com.Security.RefreshTokenException;
import com.User.UserAlreadyExistsException;
import com.User.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/test/endpoint");
    }

    @Nested
    @DisplayName("404 Not Found Tests")
    class NotFoundTests {

        @Test
        @DisplayName("Should handle UserNotFoundException")
        void shouldHandleUserNotFoundException() {
            UserNotFoundException ex = new UserNotFoundException(999L);

            ResponseEntity<ApiError> response = exceptionHandler.handleNotFound(ex, request);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals(404, response.getBody().getStatus());
            assertEquals("User not found with id=999", response.getBody().getMessage());
        }

        @Test
        @DisplayName("Should handle HotelNotFoundException")
        void shouldHandleHotelNotFoundException() {
            HotelNotFoundException ex = new HotelNotFoundException(1L);

            ResponseEntity<ApiError> response = exceptionHandler.handleNotFound(ex, request);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals(404, response.getBody().getStatus());
        }

        @Test
        @DisplayName("Should handle BookingNotFoundException")
        void shouldHandleBookingNotFoundException() {
            BookingNotFoundException ex = new BookingNotFoundException(1L);

            ResponseEntity<ApiError> response = exceptionHandler.handleNotFound(ex, request);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("409 Conflict Tests")
    class ConflictTests {

        @Test
        @DisplayName("Should handle UserAlreadyExistsException")
        void shouldHandleUserAlreadyExistsException() {
            UserAlreadyExistsException ex = new UserAlreadyExistsException("User already exists");

            ResponseEntity<ApiError> response = exceptionHandler.handleConflict(ex, request);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals(409, response.getBody().getStatus());
        }

        @Test
        @DisplayName("Should handle HotelAlreadyExistsException")
        void shouldHandleHotelAlreadyExistsException() {
            HotelAlreadyExistsException ex = new HotelAlreadyExistsException("Hotel");

            ResponseEntity<ApiError> response = exceptionHandler.handleConflict(ex, request);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("400 Bad Request Tests")
    class BadRequestTests {

        @Test
        @DisplayName("Should handle BookingBadRequestException")
        void shouldHandleBookingBadRequestException() {
            BookingBadRequestException ex = new BookingBadRequestException("Invalid booking");

            ResponseEntity<ApiError> response = exceptionHandler.handleBadRequest(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(400, response.getBody().getStatus());
        }

        @Test
        @DisplayName("Should handle IllegalArgumentException")
        void shouldHandleIllegalArgumentException() {
            IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

            ResponseEntity<ApiError> response = exceptionHandler.handleIllegalArgument(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Invalid argument", response.getBody().getMessage());
        }

        @Test
        @DisplayName("Should handle MethodArgumentNotValidException")
        void shouldHandleMethodArgumentNotValidException() {
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("object", "field", "must not be blank");

            when(ex.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

            ResponseEntity<ApiError> response = exceptionHandler.handleValidationError(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("field: must not be blank", response.getBody().getMessage());
        }

        @Test
        @DisplayName("Should handle ConstraintViolationException")
        void shouldHandleConstraintViolationException() {
            ConstraintViolation<?> violation = mock(ConstraintViolation.class);
            Path path = mock(Path.class);
            when(path.toString()).thenReturn("field");
            when(violation.getPropertyPath()).thenReturn(path);
            when(violation.getMessage()).thenReturn("must not be null");

            Set<ConstraintViolation<?>> violations = new HashSet<>();
            violations.add(violation);

            ConstraintViolationException ex = new ConstraintViolationException(violations);

            ResponseEntity<ApiError> response = exceptionHandler.handleConstraintViolation(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Security Exception Tests")
    class SecurityExceptionTests {

        @Test
        @DisplayName("Should handle BadCredentialsException")
        void shouldHandleBadCredentialsException() {
            BadCredentialsException ex = new BadCredentialsException("Bad credentials");

            ResponseEntity<ApiError> response = exceptionHandler.handleBadCredentials(ex, request);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid username or password", response.getBody().getMessage());
        }

        @Test
        @DisplayName("Should handle AccessDeniedException")
        void shouldHandleAccessDeniedException() {
            AccessDeniedException ex = new AccessDeniedException("Access denied");

            ResponseEntity<ApiError> response = exceptionHandler.handleAccessDenied(ex, request);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals("You do not have permission to access this resource", response.getBody().getMessage());
        }

        @Test
        @DisplayName("Should handle LockedException")
        void shouldHandleLockedException() {
            LockedException ex = new LockedException("Account locked");

            ResponseEntity<ApiError> response = exceptionHandler.handleAccountLocked(ex, request);

            assertEquals(HttpStatus.LOCKED, response.getStatusCode());
            assertEquals("Account locked", response.getBody().getMessage());
        }

        @Test
        @DisplayName("Should handle RefreshTokenException")
        void shouldHandleRefreshTokenException() {
            RefreshTokenException ex = new RefreshTokenException("Invalid token");

            ResponseEntity<ApiError> response = exceptionHandler.handleRefreshTokenError(ex, request);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals("Invalid token", response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("500 Internal Server Error Tests")
    class InternalServerErrorTests {

        @Test
        @DisplayName("Should handle generic Exception")
        void shouldHandleGenericException() {
            Exception ex = new Exception("Unexpected error");

            ResponseEntity<ApiError> response = exceptionHandler.handleGlobalException(ex, request);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Something went wrong", response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("Response Structure Tests")
    class ResponseStructureTests {

        @Test
        @DisplayName("Should include all required fields in ApiError")
        void shouldIncludeAllRequiredFields() {
            UserNotFoundException ex = new UserNotFoundException(999L);

            ResponseEntity<ApiError> response = exceptionHandler.handleNotFound(ex, request);

            ApiError error = response.getBody();
            assertNotNull(error);
            assertNotNull(error.getTimestamp());
            assertEquals(404, error.getStatus());
            assertEquals("Not Found", error.getError());
            assertEquals("User not found with id=999", error.getMessage());
            assertEquals("/test/endpoint", error.getPath());
        }
    }
}
