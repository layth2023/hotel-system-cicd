package com.RoomType;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomTypeServiceImpl implements RoomTypeServiceInt {

    private final RoomTypeRepository repository;

    public RoomTypeServiceImpl(RoomTypeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<RoomTypeResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(RoomTypeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomTypeResponseDTO findById(Long id) {
        RoomType roomType = repository.findById(id)
                .orElseThrow(() -> new RoomTypeNotFoundException(id));
        return RoomTypeMapper.toDto(roomType);
    }

    public RoomTypeResponseDTO create(RoomTypeRequestDTO dto) {
        if (repository.existsByName(dto.getName())) {
            throw new RoomTypeAlreadyExistsException(dto.getName());
        }

        RoomType roomType = RoomTypeMapper.toEntity(dto);
        RoomType saved = repository.save(roomType);
        return RoomTypeMapper.toDto(saved);
    }

    public RoomTypeResponseDTO update(Long id, RoomTypeRequestDTO dto) {
        RoomType existing = repository.findById(id)
                .orElseThrow(() -> new RoomTypeNotFoundException(id));

        existing.setName(dto.getName());
        existing.setBeds(dto.getBeds());
        existing.setCapacity(dto.getCapacity());
        existing.setPricePerNight(dto.getPricePerNight());
        existing.setSeasonalPrice(dto.getSeasonalPrice());
        existing.setFreeCancellationHours(dto.getFreeCancellationHours());
        existing.setCancellationRules(dto.getCancellationRules());
        existing.setDescription(dto.getDescription());
        existing.setImagePath(dto.getImagePath());

        RoomType updated = repository.save(existing);
        return RoomTypeMapper.toDto(updated);
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new RoomTypeNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
