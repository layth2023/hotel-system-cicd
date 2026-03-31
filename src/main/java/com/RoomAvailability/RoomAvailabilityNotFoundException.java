package com.RoomAvailability;

public class RoomAvailabilityNotFoundException extends RuntimeException {

    public RoomAvailabilityNotFoundException(Long id) {
        super("Room availability not found with id: " + id);
    }

    public RoomAvailabilityNotFoundException(String message) {
        super(message);
    }
}
