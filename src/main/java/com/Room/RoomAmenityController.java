package com.Room;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Room Amenity management.
 * Handles amenity assignment with quantity and pricing for rooms.
 */
@RestController
@RequestMapping("/rooms/{roomId}/amenities")
@Tag(name = "Room Amenities", description = "Room amenity management with quantity and pricing")
public class RoomAmenityController {

    private final RoomAmenityService roomAmenityService;

    public RoomAmenityController(RoomAmenityService roomAmenityService) {
        this.roomAmenityService = roomAmenityService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Add amenity to room", description = "Add an amenity to a room with quantity and price")
    public ResponseEntity<RoomAmenityResponseDTO> addAmenityToRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody RoomAmenityRequestDTO dto) {
        RoomAmenityResponseDTO created = roomAmenityService.addAmenityToRoom(roomId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Get room amenities", description = "Get all amenities assigned to a room with quantities")
    public List<RoomAmenityResponseDTO> getAmenitiesByRoom(@PathVariable Long roomId) {
        return roomAmenityService.getAmenitiesByRoom(roomId);
    }

    @GetMapping("/{amenityId}")
    @Operation(summary = "Get specific room amenity", description = "Get details of a specific amenity in a room")
    public RoomAmenityResponseDTO getByRoomAndAmenity(
            @PathVariable Long roomId,
            @PathVariable Long amenityId) {
        return roomAmenityService.getByRoomAndAmenity(roomId, amenityId);
    }

    @PutMapping("/{amenityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update room amenity", description = "Update quantity or price of an amenity in a room")
    public RoomAmenityResponseDTO updateRoomAmenity(
            @PathVariable Long roomId,
            @PathVariable Long amenityId,
            @Valid @RequestBody RoomAmenityRequestDTO dto) {
        return roomAmenityService.updateRoomAmenity(roomId, amenityId, dto);
    }

    @DeleteMapping("/{amenityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove amenity from room", description = "Remove an amenity from a room")
    public void removeAmenityFromRoom(
            @PathVariable Long roomId,
            @PathVariable Long amenityId) {
        roomAmenityService.removeAmenityFromRoom(roomId, amenityId);
    }

    @GetMapping("/total-price")
    @Operation(summary = "Calculate total amenity price", description = "Calculate the total daily price of all amenities in a room")
    public Map<String, Object> calculateTotalAmenityPrice(@PathVariable Long roomId) {
        BigDecimal totalPrice = roomAmenityService.calculateTotalAmenityPrice(roomId);
        return Map.of("roomId", roomId, "totalAmenityPrice", totalPrice);
    }
}
