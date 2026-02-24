package com.Amenity;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    public AmenityServiceImpl(AmenityRepository amenityRepository,
                              AmenityMapper amenityMapper) {
        this.amenityRepository = amenityRepository;
        this.amenityMapper = amenityMapper;
    }

    @Override
    public AmenityResponseDTO create(AmenityRequestDTO requestDTO) {

        String normalizedName = normalize(requestDTO.getName());

        if (amenityRepository.existsByNameInsensitive(normalizedName)) {
            throw new AmenityAlreadyExistsException(normalizedName);
        }

        requestDTO.setName(normalizedName);

        Amenity amenity = amenityMapper.toEntity(requestDTO);
        Amenity saved = amenityRepository.save(amenity);

        return amenityMapper.toResponseDTO(saved);
    }

    @Override
    public AmenityResponseDTO getById(Long id) {

        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new AmenityNotFoundException(id));

        return amenityMapper.toResponseDTO(amenity);
    }

    @Override
    public Page<AmenityResponseDTO> getAllActive(Pageable pageable) {
        return amenityRepository.findAllByIsActiveTrue(pageable)
                .map(amenityMapper::toResponseDTO);
    }

    @Override
    public Page<AmenityResponseDTO> getAll(Pageable pageable) {
        return amenityRepository.findAll(pageable)
                .map(amenityMapper::toResponseDTO);
    }

    @Override
    public Page<AmenityResponseDTO> getAllInactive(Pageable pageable) {
        return amenityRepository.findAllByIsActiveFalse(pageable)
                .map(amenityMapper::toResponseDTO);
    }

    @Override
    public AmenityResponseDTO update(Long id, AmenityRequestDTO requestDTO) {

        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new AmenityNotFoundException(id));

        String normalizedName = normalize(requestDTO.getName());

        if (amenityRepository.existsByNameInsensitiveAndIdNot(normalizedName, id)) {
            throw new AmenityAlreadyExistsException(normalizedName);
        }

        requestDTO.setName(normalizedName);

        amenityMapper.updateEntity(amenity, requestDTO);
        Amenity saved = amenityRepository.save(amenity);

        return amenityMapper.toResponseDTO(saved);
    }

    @Override
    public void softDelete(Long id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new AmenityNotFoundException(id));

        amenity.setIsActive(false);
    }

    @Override
    public void hardDelete(Long id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new AmenityNotFoundException(id));

        amenityRepository.delete(amenity);
    }

    @Override
    public void reactivate(Long id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new AmenityNotFoundException(id));

        amenity.setIsActive(true);
    }

    private String normalize(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Amenity name cannot be null");
        }
        return name.trim();
    }
}