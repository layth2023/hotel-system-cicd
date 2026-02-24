package com.Amenity;

import org.springframework.stereotype.Component;

@Component
public class AmenityMapper {

    public Amenity toEntity(AmenityRequestDTO dto) {

        Amenity amenity = new Amenity();

        amenity.setName(dto.getName() == null ? null : dto.getName().trim());
        amenity.setDescription(dto.getDescription() == null ? null : dto.getDescription().trim());

        if (dto.getIsActive() == null) {
            amenity.setIsActive(true);
        } else {
            amenity.setIsActive(dto.getIsActive());
        }

        return amenity;
    }

    public void updateEntity(Amenity amenity, AmenityRequestDTO dto) {

        if (dto.getName() != null) {
            amenity.setName(dto.getName().trim());
        }

        if (dto.getDescription() != null) {
            amenity.setDescription(dto.getDescription().trim());
        } else {
            amenity.setDescription(null);
        }

        if (dto.getIsActive() != null) {
            amenity.setIsActive(dto.getIsActive());
        }
    }

    public AmenityResponseDTO toResponseDTO(Amenity amenity) {
        return new AmenityResponseDTO(
                amenity.getId(),
                amenity.getName(),
                amenity.getDescription(),
                amenity.getIsActive()
        );
    }
}