package com.Amenity;

import lombok.Getter;

@Getter
public class AmenityResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Boolean isActive;

    public AmenityResponseDTO(Long id, String name, String description, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }

}