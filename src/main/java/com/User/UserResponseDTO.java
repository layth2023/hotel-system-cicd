package com.User;

import lombok.Getter;

import java.util.Set;

@Getter
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

}