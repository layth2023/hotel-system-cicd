package com.Mapper;

import com.DTO.HotelRequestDTO;
import com.DTO.HotelResponseDTO;
import com.Entity.Hotel;
import org.springframework.stereotype.Component;

@Component
public class HotelMapper {

    public Hotel toEntity(HotelRequestDTO dto) {
        Hotel hotel = new Hotel();
        hotel.setName(dto.getName());
        hotel.setAddress(dto.getAddress());
        hotel.setCity(dto.getCity());
        hotel.setCountry(dto.getCountry());
        hotel.setPhone(dto.getPhone());
        hotel.setEmail(dto.getEmail());
        hotel.setDescription(dto.getDescription());
        return hotel;
    }

    public HotelResponseDTO toDTO(Hotel hotel) {
        HotelResponseDTO dto = new HotelResponseDTO();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setAddress(hotel.getAddress());
        dto.setCity(hotel.getCity());
        dto.setCountry(hotel.getCountry());
        dto.setPhone(hotel.getPhone());
        dto.setEmail(hotel.getEmail());
        dto.setDescription(hotel.getDescription());
        return dto;
    }


}
