package com.RoomAvailability;

import com.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/room-availability")
public class RoomAvailabilityController {

    private final RoomAvailabilityService roomAvailabilityService;

    public RoomAvailabilityController(RoomAvailabilityService roomAvailabilityService) {
        this.roomAvailabilityService = roomAvailabilityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public RoomAvailabilityResponseDTO create(@Valid @RequestBody RoomAvailabilityRequestDTO requestDTO) {
        return roomAvailabilityService.create(requestDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public RoomAvailabilityResponseDTO getById(@PathVariable Long id) {
        return roomAvailabilityService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public RoomAvailabilityResponseDTO update(@PathVariable Long id,
                                              @Valid @RequestBody RoomAvailabilityRequestDTO requestDTO) {
        return roomAvailabilityService.update(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public void delete(@PathVariable Long id) {
        roomAvailabilityService.delete(id);
    }

    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public PagedResponse<RoomAvailabilityResponseDTO> getByRoomId(@PathVariable Long roomId,
                                                                   Pageable pageable) {
        Page<RoomAvailabilityResponseDTO> page = roomAvailabilityService.getByRoomId(roomId, pageable);
        return PagedResponse.from(page);
    }

    @GetMapping("/room/{roomId}/range")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public List<RoomAvailabilityResponseDTO> getByRoomIdAndDateRange(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return roomAvailabilityService.getByRoomIdAndDateRange(roomId, startDate, endDate);
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<RoomAvailabilityResponseDTO> bulkCreateOrUpdate(
            @Valid @RequestBody RoomAvailabilityBulkRequestDTO requestDTO) {
        return roomAvailabilityService.bulkCreateOrUpdate(requestDTO);
    }

    @DeleteMapping("/room/{roomId}/range")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public void bulkDelete(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        roomAvailabilityService.bulkDelete(roomId, startDate, endDate);
    }
}
