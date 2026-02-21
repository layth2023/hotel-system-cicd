package com.Role;

import lombok.Getter;

@Getter
public class RoleResponseDTO {

    private Long id;
    private String name;
    private String description;
    private boolean active;
    private long usersCount;

    public RoleResponseDTO(Long id,
                           String name,
                           String description,
                           boolean active,
                           long usersCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.usersCount = usersCount;
    }

}