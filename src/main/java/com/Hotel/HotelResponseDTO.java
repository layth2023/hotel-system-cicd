package com.Hotel;

import lombok.Getter;

import java.util.Set;

@Getter
public class HotelResponseDTO {

    private Long id;
    private String name;
    private String address;
    private String city;
    private String country;
    private String phone;
    private String email;

    private Set<String> amenities;

    public HotelResponseDTO(Long id, String name, String address, String city, String country,
                            String phone, String email, Set<String> amenities) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.country = country;
        this.phone = phone;
        this.email = email;
        this.amenities = amenities;
    }

}