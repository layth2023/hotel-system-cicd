package com.Amenity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AmenityRequestDTO {

    @NotBlank(message = "Amenity name cannot be empty")
    @Size(min = 1, max = 80, message = "Amenity name must be between 1 and 80")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    private Boolean isActive;
}