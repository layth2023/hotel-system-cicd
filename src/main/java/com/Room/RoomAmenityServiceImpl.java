package com.Room;

import com.Amenity.Amenity;
import com.Amenity.AmenityNotFoundException;
import com.Amenity.AmenityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for RoomAmenity operations.
 */
@Service
@Transactional
public class RoomAmenityServiceImpl implements RoomAmenityService {

    private final RoomAmenityRepository roomAmenityRepository;
    private final RoomRepository roomRepository;
    private final AmenityRepository amenityRepository;

    public RoomAmenityServiceImpl(RoomAmenityRepository roomAmenityRepository,
                                   RoomRepository roomRepository,
                                   AmenityRepository amenityRepository) {
        this.roomAmenityRepository = roomAmenityRepository;
        this.roomRepository = roomRepository;
        this.amenityRepository = amenityRepository;
    }

    @Override
    public RoomAmenityResponseDTO addAmenityToRoom(Long roomId, RoomAmenityRequestDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        Amenity amenity = amenityRepository.findById(dto.getAmenityId())
                .orElseThrow(() -> new AmenityNotFoundException(dto.getAmenityId()));

        // Check if amenity already exists for this room
        if (roomAmenityRepository.existsByRoomIdAndAmenityId(roomId, dto.getAmenityId())) {
            throw new RoomBadRequestException("Amenity already exists for this room. Use update instead.");
        }

        RoomAmenity roomAmenity = new RoomAmenity();
        roomAmenity.setRoom(room);
        roomAmenity.setAmenity(amenity);
        roomAmenity.setQuantity(dto.getQuantity());
        roomAmenity.setPricePerUnit(dto.getPricePerUnit() != null ? dto.getPricePerUnit() : BigDecimal.ZERO);
        roomAmenity.setNotes(dto.getNotes());

        RoomAmenity saved = roomAmenityRepository.save(roomAmenity);
        return toResponseDTO(saved);
    }

    @Override
    public RoomAmenityResponseDTO updateRoomAmenity(Long roomId, Long amenityId, RoomAmenityRequestDTO dto) {
        RoomAmenity roomAmenity = roomAmenityRepository.findByRoomIdAndAmenityId(roomId, amenityId)
                .orElseThrow(() -> new RoomNotFoundException("Room amenity not found for room: " + roomId + " and amenity: " + amenityId));

        if (dto.getQuantity() != null) {
            roomAmenity.setQuantity(dto.getQuantity());
        }
        if (dto.getPricePerUnit() != null) {
            roomAmenity.setPricePerUnit(dto.getPricePerUnit());
        }
        if (dto.getNotes() != null) {
            roomAmenity.setNotes(dto.getNotes());
        }

        RoomAmenity updated = roomAmenityRepository.save(roomAmenity);
        return toResponseDTO(updated);
    }

    @Override
    public void removeAmenityFromRoom(Long roomId, Long amenityId) {
        if (!roomAmenityRepository.existsByRoomIdAndAmenityId(roomId, amenityId)) {
            throw new RoomNotFoundException("Room amenity not found for room: " + roomId + " and amenity: " + amenityId);
        }
        roomAmenityRepository.deleteByRoomIdAndAmenityId(roomId, amenityId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomAmenityResponseDTO> getAmenitiesByRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
        return roomAmenityRepository.findByRoomIdWithAmenity(roomId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoomAmenityResponseDTO getByRoomAndAmenity(Long roomId, Long amenityId) {
        RoomAmenity roomAmenity = roomAmenityRepository.findByRoomIdAndAmenityId(roomId, amenityId)
                .orElseThrow(() -> new RoomNotFoundException("Room amenity not found for room: " + roomId + " and amenity: " + amenityId));
        return toResponseDTO(roomAmenity);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalAmenityPrice(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
        return roomAmenityRepository.calculateTotalAmenityPriceForRoom(roomId);
    }

    private RoomAmenityResponseDTO toResponseDTO(RoomAmenity roomAmenity) {
        RoomAmenityResponseDTO dto = new RoomAmenityResponseDTO();
        dto.setId(roomAmenity.getId());
        dto.setRoomId(roomAmenity.getRoom().getId());
        dto.setRoomNumber(roomAmenity.getRoom().getRoomNumber());
        dto.setAmenityId(roomAmenity.getAmenity().getId());
        dto.setAmenityName(roomAmenity.getAmenity().getName());
        dto.setAmenityDescription(roomAmenity.getAmenity().getDescription());
        dto.setQuantity(roomAmenity.getQuantity());
        dto.setPricePerUnit(roomAmenity.getPricePerUnit());
        dto.setTotalPrice(roomAmenity.getTotalPrice());
        dto.setNotes(roomAmenity.getNotes());
        return dto;
    }
}
