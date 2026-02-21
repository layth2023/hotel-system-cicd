package com.Room;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomServiceImpl roomService;

    public RoomController(RoomServiceImpl roomService) {
        this.roomService = roomService;
    }

    // GET /rooms -> list all rooms
    @GetMapping
    public List<RoomResponseDTO> all() {
        return roomService.findAll();
    }


    @PostMapping
    public ResponseEntity<RoomResponseDTO> create(
            @RequestBody RoomRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder
    ) {
        RoomResponseDTO saved = roomService.create(requestDTO);

        URI location = uriBuilder
                .path("/rooms/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    // GET /rooms/{id} -> get by id
    @GetMapping("/{id}")
    public RoomResponseDTO one(@PathVariable Long id) {
        return roomService.findById(id);
    }

    // PUT /rooms/{id} -> update room
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> update(
            @PathVariable Long id,
            @RequestBody RoomRequestDTO requestDTO
    ) {
        RoomResponseDTO updated = roomService.update(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE /rooms/{id} -> delete room
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // PUT /rooms/{roomId}/room-type/{roomTypeId} -> assign RoomType
    @PutMapping("/{roomId}/room-type/{roomTypeId}")
    public RoomResponseDTO assignRoomType(
            @PathVariable Long roomId,
            @PathVariable Long roomTypeId
    ) {
        return roomService.assignRoomType(roomId, roomTypeId);
    }

}
