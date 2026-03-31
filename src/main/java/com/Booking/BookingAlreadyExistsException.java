package com.Booking;

/**
 * Exception thrown when a booking conflict exists.
 */
public class BookingAlreadyExistsException extends RuntimeException {

    public BookingAlreadyExistsException(String message) {
        super(message);
    }

    public BookingAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
