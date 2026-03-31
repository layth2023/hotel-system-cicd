package com.BookingGuest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings/{bookingId}/guests")
public class BookingGuestController {

    private final BookingGuestService bookingGuestService;

    public BookingGuestController(BookingGuestService bookingGuestService) {
        this.bookingGuestService = bookingGuestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public BookingGuestResponseDTO addGuest(@PathVariable Long bookingId,
                                            @Valid @RequestBody BookingGuestRequestDTO requestDTO) {
        return bookingGuestService.addGuest(bookingId, requestDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public List<BookingGuestResponseDTO> getGuests(@PathVariable Long bookingId) {
        return bookingGuestService.getGuestsByBookingId(bookingId);
    }

    @GetMapping("/{guestId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public BookingGuestResponseDTO getGuest(@PathVariable Long bookingId,
                                            @PathVariable Long guestId) {
        return bookingGuestService.getById(bookingId, guestId);
    }

    @GetMapping("/primary")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public BookingGuestResponseDTO getPrimaryGuest(@PathVariable Long bookingId) {
        return bookingGuestService.getPrimaryGuest(bookingId);
    }

    @PutMapping("/{guestId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public BookingGuestResponseDTO updateGuest(@PathVariable Long bookingId,
                                               @PathVariable Long guestId,
                                               @Valid @RequestBody BookingGuestRequestDTO requestDTO) {
        return bookingGuestService.update(bookingId, guestId, requestDTO);
    }

    @DeleteMapping("/{guestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public void deleteGuest(@PathVariable Long bookingId,
                            @PathVariable Long guestId) {
        bookingGuestService.delete(bookingId, guestId);
    }
}
