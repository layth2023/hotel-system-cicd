package com.Role;

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public long getUsersCount() {
        return usersCount;
    }
}
