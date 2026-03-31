package com.Amenity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AmenityRequestDTO {

    @NotBlank(message = "Amenity name cannot be empty")
    @Size(min = 1, max = 80, message = "Amenity name must be between 1 and 80")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    private Boolean isActive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
