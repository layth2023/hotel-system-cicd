package com.Room;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for RoomAmenity operations.
 */
public interface RoomAmenityService {

    RoomAmenityResponseDTO addAmenityToRoom(Long roomId, RoomAmenityRequestDTO dto);

    RoomAmenityResponseDTO updateRoomAmenity(Long roomId, Long amenityId, RoomAmenityRequestDTO dto);

    void removeAmenityFromRoom(Long roomId, Long amenityId);

    List<RoomAmenityResponseDTO> getAmenitiesByRoom(Long roomId);

    RoomAmenityResponseDTO getByRoomAndAmenity(Long roomId, Long amenityId);

    BigDecimal calculateTotalAmenityPrice(Long roomId);
}
