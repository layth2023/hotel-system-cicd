package com.RoomType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/room-types")
public class RoomTypeController {

    private final RoomTypeServiceImpl roomTypeService;

    public RoomTypeController(RoomTypeServiceImpl roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    // GET /room-types -> list all
    @GetMapping
    public List<RoomTypeResponseDTO> all() {
        return roomTypeService.findAll();
    }

    // POST /room-types -> create new
    @PostMapping
    public ResponseEntity<RoomTypeResponseDTO> create(
            @RequestBody RoomTypeRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder
    ) {
        RoomTypeResponseDTO saved = roomTypeService.create(requestDTO);

        URI location = uriBuilder
                .path("/room-types/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    // GET /room-types/{id} -> get by id
    @GetMapping("/{id}")
    public RoomTypeResponseDTO one(@PathVariable Long id) {
        return roomTypeService.findById(id);
    }

    // PUT /room-types/{id} -> update existing
    @PutMapping("/{id}")
    public ResponseEntity<RoomTypeResponseDTO> update(
            @PathVariable Long id,
            @RequestBody RoomTypeRequestDTO requestDTO
    ) {
        RoomTypeResponseDTO updated = roomTypeService.update(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE /room-types/{id} -> delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomTypeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
