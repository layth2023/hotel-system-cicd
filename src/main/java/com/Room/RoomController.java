package com.Room;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Room management.
 * Provides CRUD operations for rooms with role-based access control.
 */
@RestController
@RequestMapping("/rooms")
@Tag(name = "Rooms", description = "Room management endpoints")
public class RoomController {

    private final RoomServiceImpl roomService;

    public RoomController(RoomServiceImpl roomService) {
        this.roomService = roomService;
    }

    /**
     * Get all rooms (public access)
     */
    @GetMapping
    @Operation(summary = "Get all rooms", description = "Retrieve list of all rooms")
    public List<RoomResponseDTO> all() {
        return roomService.findAll();
    }

    /**
     * Create a new room (ADMIN or MANAGER only)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create room", description = "Create a new room (Admin/Manager only)")
    public ResponseEntity<RoomResponseDTO> create(
            @Valid @RequestBody RoomRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder
    ) {
        RoomResponseDTO saved = roomService.create(requestDTO);

        URI location = uriBuilder
                .path("/rooms/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    /**
     * Get room by ID (public access)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID", description = "Retrieve a specific room by ID")
    public RoomResponseDTO one(@PathVariable Long id) {
        return roomService.findById(id);
    }

    /**
     * Update room (ADMIN or MANAGER only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update room", description = "Update an existing room (Admin/Manager only)")
    public ResponseEntity<RoomResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequestDTO requestDTO
    ) {
        RoomResponseDTO updated = roomService.update(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete room (ADMIN only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete room", description = "Delete a room (Admin only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assign room type to a room (ADMIN or MANAGER only)
     */
    @PutMapping("/{roomId}/room-type/{roomTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Assign room type", description = "Assign a room type to a room (Admin/Manager only)")
    public RoomResponseDTO assignRoomType(
            @PathVariable Long roomId,
            @PathVariable Long roomTypeId
    ) {
        return roomService.assignRoomType(roomId, roomTypeId);
    }

    /**
     * Search for available rooms
     */
    @GetMapping("/search")
    @Operation(summary = "Search rooms", description = "Search for available rooms with filters")
    public List<RoomResponseDTO> searchRooms(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String roomTypeName,
            @RequestParam(required = false) String amenity
    ) {
        RoomSearchRequestDTO searchRequest = new RoomSearchRequestDTO();
        searchRequest.setHotelId(hotelId);
        searchRequest.setCheckInDate(checkInDate);
        searchRequest.setCheckOutDate(checkOutDate);
        searchRequest.setGuests(guests);
        searchRequest.setMinPrice(minPrice);
        searchRequest.setMaxPrice(maxPrice);
        searchRequest.setRoomTypeName(roomTypeName);
        searchRequest.setAmenity(amenity);

        return roomService.searchRooms(searchRequest);
    }

    /**
     * Get available rooms for a date range
     */
    @GetMapping("/available")
    @Operation(summary = "Get available rooms", description = "Get all available rooms for a date range")
    public List<RoomResponseDTO> getAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false, defaultValue = "1") Integer guests
    ) {
        return roomService.findAvailableRooms(checkInDate, checkOutDate, guests);
    }
}
