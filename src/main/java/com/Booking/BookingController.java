package com.Booking;

import com.PagedResponse;
import com.Security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ==========================================================
    // CRUD Operations
    // ==========================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public BookingResponseDTO create(@Valid @RequestBody BookingRequestDTO requestDTO,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        return bookingService.create(requestDTO, userDetails.getId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public BookingResponseDTO getById(@PathVariable Long id) {
        return bookingService.getById(id);
    }

    @GetMapping("/confirmation/{confirmationNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public BookingResponseDTO getByConfirmationNumber(@PathVariable String confirmationNumber) {
        return bookingService.getByConfirmationNumber(confirmationNumber);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public BookingResponseDTO update(@PathVariable Long id,
                                     @Valid @RequestBody BookingRequestDTO requestDTO) {
        return bookingService.update(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        bookingService.delete(id);
    }

    // ==========================================================
    // User Bookings
    // ==========================================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public PagedResponse<BookingResponseDTO> getMyBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        Page<BookingResponseDTO> page = bookingService.getByUserId(userDetails.getId(), pageable);
        return PagedResponse.from(page);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public PagedResponse<BookingResponseDTO> getUpcomingBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        Page<BookingResponseDTO> page = bookingService.getUpcomingByUserId(userDetails.getId(), pageable);
        return PagedResponse.from(page);
    }

    // ==========================================================
    // Admin/Manager Queries
    // ==========================================================

    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public PagedResponse<BookingResponseDTO> getByRoomId(@PathVariable Long roomId,
                                                          Pageable pageable) {
        Page<BookingResponseDTO> page = bookingService.getByRoomId(roomId, pageable);
        return PagedResponse.from(page);
    }

    @GetMapping("/hotel/{hotelId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public PagedResponse<BookingResponseDTO> getByHotelId(@PathVariable Long hotelId,
                                                           Pageable pageable) {
        Page<BookingResponseDTO> page = bookingService.getByHotelId(hotelId, pageable);
        return PagedResponse.from(page);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public PagedResponse<BookingResponseDTO> getByStatus(@PathVariable BookingStatus status,
                                                          Pageable pageable) {
        Page<BookingResponseDTO> page = bookingService.getByStatus(status, pageable);
        return PagedResponse.from(page);
    }

    @GetMapping("/today/check-ins")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<BookingResponseDTO> getTodayCheckIns() {
        return bookingService.getTodayCheckIns();
    }

    @GetMapping("/today/check-outs")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<BookingResponseDTO> getTodayCheckOuts() {
        return bookingService.getTodayCheckOuts();
    }

    // ==========================================================
    // Status Changes
    // ==========================================================

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public BookingResponseDTO cancel(@PathVariable Long id,
                                     @RequestParam(required = false) String reason) {
        return bookingService.cancel(id, reason);
    }

    @PatchMapping("/{id}/check-in")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public BookingResponseDTO checkIn(@PathVariable Long id) {
        return bookingService.checkIn(id);
    }

    @PatchMapping("/{id}/check-out")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public BookingResponseDTO checkOut(@PathVariable Long id) {
        return bookingService.checkOut(id);
    }

    @PatchMapping("/{id}/no-show")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public BookingResponseDTO markNoShow(@PathVariable Long id) {
        return bookingService.markNoShow(id);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public BookingResponseDTO confirm(@PathVariable Long id) {
        return bookingService.confirm(id);
    }
}
