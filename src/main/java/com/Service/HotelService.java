package com.Service;

import com.DTO.HotelRequestDTO;
import com.DTO.HotelResponseDTO;
import com.Entity.Hotel;
import com.Exception.ResourceNotFoundException;
import com.Mapper.HotelMapper;
import com.Repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HotelService implements HotelServiceInt {

    private final HotelRepository repository;
    private final HotelMapper mapper;

    public HotelService(HotelRepository repository, HotelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HotelResponseDTO create(HotelRequestDTO dto) {
        Hotel hotel = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(hotel));
    }

    @Override
    @Transactional(readOnly = true)
    public HotelResponseDTO findById(Long id) {
        Hotel hotel = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        return mapper.toDTO(hotel);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
