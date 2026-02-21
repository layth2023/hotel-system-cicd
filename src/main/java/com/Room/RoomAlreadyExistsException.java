package com.Room;

public class RoomAlreadyExistsException extends RuntimeException {

    public RoomAlreadyExistsException(String roomNumber) {
        super("Room already exists with room number: " + roomNumber);
    }
}
