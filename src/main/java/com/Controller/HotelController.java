package com.Controller;

import com.DTO.HotelRequestDTO;
import com.DTO.HotelResponseDTO;
import com.Service.HotelService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService service;

    public HotelController(HotelService service) {
        this.service = service;
    }

    @PostMapping
    public HotelResponseDTO create(@Valid @RequestBody HotelRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<HotelResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public HotelResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
