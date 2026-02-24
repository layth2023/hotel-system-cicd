package com.Hotel;

public class HotelAlreadyExistsException extends RuntimeException {
    public HotelAlreadyExistsException(String name) {
        super("Hotel already exists with name: " + name);
    }

}