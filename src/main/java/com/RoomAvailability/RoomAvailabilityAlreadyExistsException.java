package com.RoomAvailability;

import java.time.LocalDate;

public class RoomAvailabilityAlreadyExistsException extends RuntimeException {

    public RoomAvailabilityAlreadyExistsException(Long roomId, LocalDate date) {
        super("Room availability already exists for room " + roomId + " on date " + date);
    }

    public RoomAvailabilityAlreadyExistsException(String message) {
        super(message);
    }
}
