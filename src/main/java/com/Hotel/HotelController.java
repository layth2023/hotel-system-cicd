package com.Hotel;

import com.Amenity.AmenityResponseDTO;
import com.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public HotelResponseDTO create(@Valid @RequestBody HotelRequestDTO requestDTO) {
        return hotelService.create(requestDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public HotelResponseDTO getById(@PathVariable Long id) {
        return hotelService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public PagedResponse<HotelResponseDTO> getAll(Pageable pageable) {
        Page<HotelResponseDTO> page = hotelService.getAll(pageable);
        return PagedResponse.from(page);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HotelResponseDTO update(@PathVariable Long id,
                                   @Valid @RequestBody HotelRequestDTO requestDTO) {
        return hotelService.update(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        hotelService.delete(id);
    }

    // ==========================================================
    // Amenities assignment (ADMIN only)
    // ==========================================================

    @PostMapping("/{hotelId}/amenities/{amenityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void addAmenity(@PathVariable Long hotelId,
                           @PathVariable Long amenityId) {
        hotelService.addAmenity(hotelId, amenityId);
    }

    @DeleteMapping("/{hotelId}/amenities/{amenityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void removeAmenity(@PathVariable Long hotelId,
                              @PathVariable Long amenityId) {
        hotelService.removeAmenity(hotelId, amenityId);
    }

    // ==========================================================
    // Amenities list
    // ==========================================================

    // USER + ADMIN (active only)
    @GetMapping("/{hotelId}/amenities")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public PagedResponse<AmenityResponseDTO> getHotelAmenitiesActive(@PathVariable Long hotelId,
                                                                     Pageable pageable) {
        Page<AmenityResponseDTO> page = hotelService.getHotelAmenitiesActive(hotelId, pageable);
        return PagedResponse.from(page);
    }

    // ADMIN only (inactive only)
    @GetMapping("/{hotelId}/amenities/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResponse<AmenityResponseDTO> getHotelAmenitiesInactive(@PathVariable Long hotelId,
                                                                       Pageable pageable) {
        Page<AmenityResponseDTO> page = hotelService.getHotelAmenitiesInactive(hotelId, pageable);
        return PagedResponse.from(page);
    }

    // ADMIN only (all: active + inactive)
    @GetMapping("/{hotelId}/amenities/all")
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResponse<AmenityResponseDTO> getHotelAmenitiesAll(@PathVariable Long hotelId,
                                                                  Pageable pageable) {
        Page<AmenityResponseDTO> page = hotelService.getHotelAmenitiesAll(hotelId, pageable);
        return PagedResponse.from(page);
    }
}