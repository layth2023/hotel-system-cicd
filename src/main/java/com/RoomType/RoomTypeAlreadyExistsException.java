package com.RoomType;

public class RoomTypeAlreadyExistsException extends RuntimeException {

    public RoomTypeAlreadyExistsException(String name) {
        super("RoomType already exists with name: " + name);
    }
}
