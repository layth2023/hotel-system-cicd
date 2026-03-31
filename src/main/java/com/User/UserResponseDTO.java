package com.User;

import java.util.Set;

public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private Set<String> roles;

    public UserResponseDTO(Long id, String username, String email, boolean enabled, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
