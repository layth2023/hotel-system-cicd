package com.Amenity;

public class AmenityNotFoundException extends RuntimeException {
    public AmenityNotFoundException(Long id) {
        super("Amenity not found with id: " + id);
    }
}