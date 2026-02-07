package com.Service;

import com.DTO.HotelRequestDTO;
import com.DTO.HotelResponseDTO;
import com.Entity.Hotel;
import com.Mapper.HotelMapper;
import com.Repository.HotelRepository;
import com.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelRepository repository;
    private final HotelMapper mapper;

    public HotelService(HotelRepository repository, HotelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public HotelResponseDTO create(HotelRequestDTO dto) {
        Hotel hotel = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(hotel));
    }

    public List<HotelResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public HotelResponseDTO getById(Long id) {
        Hotel hotel = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        return mapper.toDTO(hotel);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }


}
