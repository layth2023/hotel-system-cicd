package com.Room;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RoomServiceInt {

    RoomResponseDTO create(RoomRequestDTO dto);

    RoomResponseDTO findById(Long id);

    List<RoomResponseDTO> findAll();

    RoomResponseDTO update(Long id, RoomRequestDTO dto);

    void deleteById(Long id);

    RoomResponseDTO assignRoomType(Long roomId, Long roomTypeId);

    // Search and filtering
    List<RoomResponseDTO> findByHotelId(Long hotelId);

    Page<RoomResponseDTO> findByHotelId(Long hotelId, Pageable pageable);

    List<RoomResponseDTO> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer guests);

    List<RoomResponseDTO> findAvailableRoomsByHotel(Long hotelId, LocalDate checkInDate, LocalDate checkOutDate, Integer guests);

    List<RoomResponseDTO> searchRooms(RoomSearchRequestDTO searchRequest);
}
