package com.Hotel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HotelRequestDTO {

    @NotBlank(message = "Hotel name cannot be empty")
    @Size(max = 120)
    private String name;

    @NotBlank(message = "Address cannot be empty")
    @Size(max = 200)
    private String address;

    private String city;
    private String country;
    private String phone;
    private String email;
}