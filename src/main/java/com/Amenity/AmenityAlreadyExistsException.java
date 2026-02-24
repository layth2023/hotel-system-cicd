package com.Amenity;

public class AmenityAlreadyExistsException extends RuntimeException {
    public AmenityAlreadyExistsException(String name) {
        super("Amenity already exists with name: " + name);
    }
}