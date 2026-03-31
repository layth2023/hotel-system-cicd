package com.BookingGuest;

public class BookingGuestNotFoundException extends RuntimeException {

    public BookingGuestNotFoundException(Long id) {
        super("Booking guest not found with id: " + id);
    }

    public BookingGuestNotFoundException(String message) {
        super(message);
    }
}
