package com.RoomType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST Controller for RoomType management.
 * Provides CRUD operations for room types with role-based access control.
 */
@RestController
@RequestMapping("/room-types")
@Tag(name = "Room Types", description = "Room type management endpoints")
public class RoomTypeController {

    private final RoomTypeServiceImpl roomTypeService;

    public RoomTypeController(RoomTypeServiceImpl roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    /**
     * Get all room types (public access)
     */
    @GetMapping
    @Operation(summary = "Get all room types", description = "Retrieve list of all room types")
    public List<RoomTypeResponseDTO> all() {
        return roomTypeService.findAll();
    }

    /**
     * Create a new room type (ADMIN only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create room type", description = "Create a new room type (Admin only)")
    public ResponseEntity<RoomTypeResponseDTO> create(
            @Valid @RequestBody RoomTypeRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder
    ) {
        RoomTypeResponseDTO saved = roomTypeService.create(requestDTO);

        URI location = uriBuilder
                .path("/room-types/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    /**
     * Get room type by ID (public access)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get room type by ID", description = "Retrieve a specific room type by ID")
    public RoomTypeResponseDTO one(@PathVariable Long id) {
        return roomTypeService.findById(id);
    }

    /**
     * Update room type (ADMIN only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update room type", description = "Update an existing room type (Admin only)")
    public ResponseEntity<RoomTypeResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomTypeRequestDTO requestDTO
    ) {
        RoomTypeResponseDTO updated = roomTypeService.update(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete room type (ADMIN only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete room type", description = "Delete a room type (Admin only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomTypeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
