package com.RoomType;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for RoomType operations.
 */
@Service
@Transactional
public class RoomTypeServiceImpl implements RoomTypeServiceInt {

    private final RoomTypeRepository repository;
    private final RoomTypeMapper mapper;

    public RoomTypeServiceImpl(RoomTypeRepository repository, RoomTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoomTypeResponseDTO findById(Long id) {
        RoomType roomType = repository.findById(id)
                .orElseThrow(() -> new RoomTypeNotFoundException(id));
        return mapper.toDto(roomType);
    }

    @Override
    public RoomTypeResponseDTO create(RoomTypeRequestDTO dto) {
        if (repository.existsByName(dto.getName())) {
            throw new RoomTypeAlreadyExistsException(dto.getName());
        }

        RoomType roomType = mapper.toEntity(dto);
        RoomType saved = repository.save(roomType);
        return mapper.toDto(saved);
    }

    @Override
    public RoomTypeResponseDTO update(Long id, RoomTypeRequestDTO dto) {
        RoomType existing = repository.findById(id)
                .orElseThrow(() -> new RoomTypeNotFoundException(id));

        mapper.updateEntity(existing, dto);
        RoomType updated = repository.save(existing);
        return mapper.toDto(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new RoomTypeNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
