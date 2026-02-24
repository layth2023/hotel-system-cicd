package com.Hotel;


import com.Amenity.Amenity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class HotelMapper {

    public Hotel toEntity(HotelRequestDTO dto) {
        Hotel hotel = new Hotel();
        hotel.setName(dto.getName() == null ? null : dto.getName().trim());
        hotel.setAddress(dto.getAddress() == null ? null : dto.getAddress().trim());
        hotel.setCity(dto.getCity());
        hotel.setCountry(dto.getCountry());
        hotel.setPhone(dto.getPhone());
        hotel.setEmail(dto.getEmail());
        return hotel;
    }

    public void updateEntity(Hotel hotel, HotelRequestDTO dto) {
        if (dto.getName() != null) hotel.setName(dto.getName().trim());
        if (dto.getAddress() != null) hotel.setAddress(dto.getAddress().trim());
        hotel.setCity(dto.getCity());
        hotel.setCountry(dto.getCountry());
        hotel.setPhone(dto.getPhone());
        hotel.setEmail(dto.getEmail());
    }

    public HotelResponseDTO toResponseDTO(Hotel hotel) {
        return new HotelResponseDTO(
                hotel.getId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getCity(),
                hotel.getCountry(),
                hotel.getPhone(),
                hotel.getEmail(),
                hotel.getAmenities().stream().map(Amenity::getName).collect(Collectors.toSet())
        );
    }
}