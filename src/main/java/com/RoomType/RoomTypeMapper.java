package com.RoomType;

import org.springframework.stereotype.Component;

/**
 * Mapper for RoomType entity and DTOs.
 */
@Component
public class RoomTypeMapper {

    public RoomType toEntity(RoomTypeRequestDTO dto) {
        RoomType e = new RoomType();
        e.setName(dto.getName());
        e.setCapacity(dto.getCapacity());
        e.setBeds(dto.getBeds());
        e.setPricePerNight(dto.getPricePerNight());
        e.setCancellationRules(dto.getCancellationRules());
        e.setDescription(dto.getDescription());
        e.setImagePath(dto.getImagePath());
        e.setSeasonalPrice(dto.getSeasonalPrice());
        e.setFreeCancellationHours(dto.getFreeCancellationHours());
        return e;
    }

    public void updateEntity(RoomType entity, RoomTypeRequestDTO dto) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getCapacity() != null) {
            entity.setCapacity(dto.getCapacity());
        }
        if (dto.getBeds() != null) {
            entity.setBeds(dto.getBeds());
        }
        if (dto.getPricePerNight() != null) {
            entity.setPricePerNight(dto.getPricePerNight());
        }
        if (dto.getCancellationRules() != null) {
            entity.setCancellationRules(dto.getCancellationRules());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getImagePath() != null) {
            entity.setImagePath(dto.getImagePath());
        }
        if (dto.getSeasonalPrice() != null) {
            entity.setSeasonalPrice(dto.getSeasonalPrice());
        }
        if (dto.getFreeCancellationHours() != null) {
            entity.setFreeCancellationHours(dto.getFreeCancellationHours());
        }
    }

    public RoomTypeResponseDTO toDto(RoomType e) {
        RoomTypeResponseDTO dto = new RoomTypeResponseDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setPricePerNight(e.getPricePerNight());
        dto.setSeasonalPrice(e.getSeasonalPrice());
        dto.setCapacity(e.getCapacity());
        dto.setFreeCancellationHours(e.getFreeCancellationHours());
        dto.setCancellationRules(e.getCancellationRules());
        dto.setImagePath(e.getImagePath());
        dto.setDescription(e.getDescription());
        dto.setBeds(e.getBeds());
        dto.setActive(e.isActive());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

    // Static method for backward compatibility
    public static RoomTypeResponseDTO toResponseDto(RoomType e) {
        RoomTypeResponseDTO dto = new RoomTypeResponseDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setPricePerNight(e.getPricePerNight());
        dto.setSeasonalPrice(e.getSeasonalPrice());
        dto.setCapacity(e.getCapacity());
        dto.setFreeCancellationHours(e.getFreeCancellationHours());
        dto.setCancellationRules(e.getCancellationRules());
        dto.setImagePath(e.getImagePath());
        dto.setDescription(e.getDescription());
        dto.setBeds(e.getBeds());
        dto.setActive(e.isActive());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }
}
