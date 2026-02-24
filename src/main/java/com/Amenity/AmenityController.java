package com.Amenity;


import com.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/amenities")
public class AmenityController {

    private final AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public AmenityResponseDTO create(@Valid @RequestBody AmenityRequestDTO requestDTO) {
        return amenityService.create(requestDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public AmenityResponseDTO getById(@PathVariable Long id) {
        return amenityService.getById(id);
    }

    // users/admin: active only
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public PagedResponse<AmenityResponseDTO> getAllActive(Pageable pageable) {
        Page<AmenityResponseDTO> page = amenityService.getAllActive(pageable);
        return PagedResponse.from(page);
    }

    // admin: active + inactive
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResponse<AmenityResponseDTO> getAll(Pageable pageable) {
        Page<AmenityResponseDTO> page = amenityService.getAll(pageable);
        return PagedResponse.from(page);
    }

    // admin: inactive only
    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResponse<AmenityResponseDTO> getAllInactive(Pageable pageable) {
        Page<AmenityResponseDTO> page = amenityService.getAllInactive(pageable);
        return PagedResponse.from(page);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AmenityResponseDTO update(@PathVariable Long id,
                                     @Valid @RequestBody AmenityRequestDTO requestDTO) {
        return amenityService.update(id, requestDTO);
    }

    // soft delete (ADMIN)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void softDelete(@PathVariable Long id) {
        amenityService.softDelete(id);
    }

    // hard delete (ADMIN)
    @DeleteMapping("/{id}/hard")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void hardDelete(@PathVariable Long id) {
        amenityService.hardDelete(id);
    }

    // reactivate (ADMIN)
    @PatchMapping("/{id}/reactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void reactivate(@PathVariable Long id) {
        amenityService.reactivate(id);
    }
}