package com.Booking;

/**
 * Exception thrown when a booking is not found.
 */
public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(Long id) {
        super("Booking not found with id: " + id);
    }

    public BookingNotFoundException(String confirmationNumber) {
        super("Booking not found with confirmation number: " + confirmationNumber);
    }

    public BookingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
