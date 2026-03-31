package com.Booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {

    // CRUD
    BookingResponseDTO create(BookingRequestDTO requestDTO, Long userId);

    BookingResponseDTO getById(Long id);

    BookingResponseDTO getByConfirmationNumber(String confirmationNumber);

    BookingResponseDTO update(Long id, BookingRequestDTO requestDTO);

    void delete(Long id);

    // User bookings
    Page<BookingResponseDTO> getByUserId(Long userId, Pageable pageable);

    Page<BookingResponseDTO> getUpcomingByUserId(Long userId, Pageable pageable);

    // Admin/Manager queries
    Page<BookingResponseDTO> getByRoomId(Long roomId, Pageable pageable);

    Page<BookingResponseDTO> getByHotelId(Long hotelId, Pageable pageable);

    Page<BookingResponseDTO> getByStatus(BookingStatus status, Pageable pageable);

    List<BookingResponseDTO> getTodayCheckIns();

    List<BookingResponseDTO> getTodayCheckOuts();

    // Status changes
    BookingResponseDTO cancel(Long id, String reason);

    BookingResponseDTO checkIn(Long id);

    BookingResponseDTO checkOut(Long id);

    BookingResponseDTO markNoShow(Long id);

    BookingResponseDTO confirm(Long id);
}
