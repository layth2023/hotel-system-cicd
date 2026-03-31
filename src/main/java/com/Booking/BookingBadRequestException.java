package com.Booking;

/**
 * Exception thrown for invalid booking operations.
 */
public class BookingBadRequestException extends RuntimeException {

    public BookingBadRequestException(String message) {
        super(message);
    }

    public BookingBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
