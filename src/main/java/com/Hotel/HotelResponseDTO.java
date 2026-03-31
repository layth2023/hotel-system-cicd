package com.Hotel;

import java.util.Set;

public class HotelResponseDTO {

    private Long id;
    private String name;
    private String address;
    private String city;
    private String country;
    private String phone;
    private String email;
    private Integer numberOfFloors;

    private Set<String> amenities;

    public HotelResponseDTO(Long id, String name, String address, String city, String country,
                            String phone, String email, Integer numberOfFloors, Set<String> amenities) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.country = country;
        this.phone = phone;
        this.email = email;
        this.numberOfFloors = numberOfFloors;
        this.amenities = amenities;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Integer getNumberOfFloors() {
        return numberOfFloors;
    }

    public Set<String> getAmenities() {
        return amenities;
    }
}
