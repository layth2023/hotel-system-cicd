package com.Room;

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(Long id) {
        super("Room not found with id: " + id);
    }

    public RoomNotFoundException(String roomNumber) {
        super("Room not found with room number: " + roomNumber);
    }
}
