package com.RoomAvailability;

import com.Room.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomAvailabilityMapper {

    public RoomAvailability toEntity(RoomAvailabilityRequestDTO dto, Room room) {
        RoomAvailability entity = new RoomAvailability();
        entity.setRoom(room);
        entity.setDate(dto.getDate());
        entity.setAvailable(dto.isAvailable());
        entity.setPrice(dto.getPrice());
        entity.setMinStay(dto.getMinStay());
        entity.setMaxStay(dto.getMaxStay());
        entity.setNotes(dto.getNotes() != null ? dto.getNotes().trim() : null);
        return entity;
    }

    public void updateEntity(RoomAvailability entity, RoomAvailabilityRequestDTO dto) {
        entity.setAvailable(dto.isAvailable());
        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }
        if (dto.getMinStay() != null) {
            entity.setMinStay(dto.getMinStay());
        }
        entity.setMaxStay(dto.getMaxStay());
        entity.setNotes(dto.getNotes() != null ? dto.getNotes().trim() : null);
    }

    public RoomAvailabilityResponseDTO toResponseDTO(RoomAvailability entity) {
        return new RoomAvailabilityResponseDTO(
                entity.getId(),
                entity.getRoom().getId(),
                entity.getRoom().getRoomNumber(),
                entity.getDate(),
                entity.isAvailable(),
                entity.getPrice(),
                entity.getMinStay(),
                entity.getMaxStay(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
