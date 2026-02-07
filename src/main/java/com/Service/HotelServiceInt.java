package com.Service;

import com.DTO.HotelRequestDTO;
import com.DTO.HotelResponseDTO;

import java.util.List;

public interface HotelServiceInt {

    List<HotelResponseDTO> findAll();
    HotelResponseDTO create(HotelRequestDTO dto);
    HotelResponseDTO findById(Long id);
    void deleteById(Long id);
}
