package com.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleRequestDTO {

    @NotBlank(message = "Role name is required")
    @Size(min = 4, max = 60, message = "Role name must be between 4 and 60 characters")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private Boolean active;

    public RoleRequestDTO() {}

}