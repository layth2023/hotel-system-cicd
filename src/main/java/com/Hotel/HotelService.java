package com.Hotel;

import com.Amenity.AmenityResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HotelService {

    HotelResponseDTO create(HotelRequestDTO requestDTO);

    HotelResponseDTO getById(Long id);

    Page<HotelResponseDTO> getAll(Pageable pageable);

    HotelResponseDTO update(Long id, HotelRequestDTO requestDTO);

    void delete(Long id);

    // Amenities list endpoints
    Page<AmenityResponseDTO> getHotelAmenitiesActive(Long hotelId, Pageable pageable);   // USER + ADMIN
    Page<AmenityResponseDTO> getHotelAmenitiesInactive(Long hotelId, Pageable pageable); // ADMIN only
    Page<AmenityResponseDTO> getHotelAmenitiesAll(Long hotelId, Pageable pageable);      // ADMIN only

    // Amenities management (ADMIN only)
    void addAmenity(Long hotelId, Long amenityId);

    void removeAmenity(Long hotelId, Long amenityId);
}