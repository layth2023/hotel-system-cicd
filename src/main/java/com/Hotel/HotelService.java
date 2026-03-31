package com.Hotel;

import com.Amenity.AmenityResponseDTO;
import com.Room.RoomResponseDTO;
import com.RoomType.RoomTypeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

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

    // Search
    Page<HotelResponseDTO> searchHotels(String city, String country, Integer minStarRating, Pageable pageable);

    // Rooms
    List<RoomResponseDTO> getHotelRooms(Long hotelId);

    List<RoomResponseDTO> getHotelAvailableRooms(Long hotelId, LocalDate checkInDate, LocalDate checkOutDate, Integer guests);

    // Room Types
    List<RoomTypeResponseDTO> getHotelRoomTypes(Long hotelId);
}