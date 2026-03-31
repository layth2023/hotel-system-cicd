package com.BookingGuest;

import java.util.List;

public interface BookingGuestService {

    BookingGuestResponseDTO addGuest(Long bookingId, BookingGuestRequestDTO requestDTO);

    BookingGuestResponseDTO getById(Long bookingId, Long guestId);

    List<BookingGuestResponseDTO> getGuestsByBookingId(Long bookingId);

    BookingGuestResponseDTO getPrimaryGuest(Long bookingId);

    BookingGuestResponseDTO update(Long bookingId, Long guestId, BookingGuestRequestDTO requestDTO);

    void delete(Long bookingId, Long guestId);
}
