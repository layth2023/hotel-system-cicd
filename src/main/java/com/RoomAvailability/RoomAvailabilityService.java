package com.RoomAvailability;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RoomAvailabilityService {

    RoomAvailabilityResponseDTO create(RoomAvailabilityRequestDTO requestDTO);

    RoomAvailabilityResponseDTO getById(Long id);

    RoomAvailabilityResponseDTO update(Long id, RoomAvailabilityRequestDTO requestDTO);

    void delete(Long id);

    Page<RoomAvailabilityResponseDTO> getByRoomId(Long roomId, Pageable pageable);

    List<RoomAvailabilityResponseDTO> getByRoomIdAndDateRange(Long roomId, LocalDate startDate, LocalDate endDate);

    List<RoomAvailabilityResponseDTO> bulkCreateOrUpdate(RoomAvailabilityBulkRequestDTO requestDTO);

    void bulkDelete(Long roomId, LocalDate startDate, LocalDate endDate);
}
