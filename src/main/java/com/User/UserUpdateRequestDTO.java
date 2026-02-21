package com.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateRequestDTO {

    @Size(min = 3, max = 60, message = "Username must be between 3 and 60 characters")
    private String username;

    @Email(message = "Email format is invalid")
    private String email;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    private Boolean enabled;

    public UserUpdateRequestDTO() {}

}