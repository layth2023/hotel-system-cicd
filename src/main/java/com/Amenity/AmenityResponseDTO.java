package com.Amenity;

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getIsActive() {
        return isActive;
    }
}
