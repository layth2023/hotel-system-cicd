package com.Amenity;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AmenityService {

    AmenityResponseDTO create(AmenityRequestDTO requestDTO);

    AmenityResponseDTO getById(Long id);

    Page<AmenityResponseDTO> getAllActive(Pageable pageable);

    Page<AmenityResponseDTO> getAll(Pageable pageable); // admin only (active + inactive)

    Page<AmenityResponseDTO> getAllInactive(Pageable pageable); // admin only

    AmenityResponseDTO update(Long id, AmenityRequestDTO requestDTO);

    void softDelete(Long id);

    void hardDelete(Long id); // admin only

    void reactivate(Long id); // admin only
}