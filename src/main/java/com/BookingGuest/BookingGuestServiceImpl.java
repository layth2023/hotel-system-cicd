package com.BookingGuest;

import com.Booking.Booking;
import com.Booking.BookingNotFoundException;
import com.Booking.BookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class BookingGuestServiceImpl implements BookingGuestService {

    private final BookingGuestRepository bookingGuestRepository;
    private final BookingGuestMapper bookingGuestMapper;
    private final BookingRepository bookingRepository;

    public BookingGuestServiceImpl(BookingGuestRepository bookingGuestRepository,
                                   BookingGuestMapper bookingGuestMapper,
                                   BookingRepository bookingRepository) {
        this.bookingGuestRepository = bookingGuestRepository;
        this.bookingGuestMapper = bookingGuestMapper;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingGuestResponseDTO addGuest(Long bookingId, BookingGuestRequestDTO requestDTO) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        // If this guest is being set as primary, unset any existing primary guest
        if (requestDTO.isPrimaryGuest()) {
            bookingGuestRepository.findByBookingIdAndPrimaryGuestTrue(bookingId)
                    .ifPresent(existingPrimary -> {
                        existingPrimary.setPrimaryGuest(false);
                        bookingGuestRepository.save(existingPrimary);
                    });
        }

        BookingGuest entity = bookingGuestMapper.toEntity(requestDTO, booking);
        BookingGuest saved = bookingGuestRepository.save(entity);

        return bookingGuestMapper.toResponseDTO(saved);
    }

    @Override
    public BookingGuestResponseDTO getById(Long bookingId, Long guestId) {
        ensureBookingExists(bookingId);

        BookingGuest entity = bookingGuestRepository.findById(guestId)
                .orElseThrow(() -> new BookingGuestNotFoundException(guestId));

        // Verify guest belongs to the booking
        if (!entity.getBooking().getId().equals(bookingId)) {
            throw new BookingGuestNotFoundException("Guest " + guestId + " not found in booking " + bookingId);
        }

        return bookingGuestMapper.toResponseDTO(entity);
    }

    @Override
    public List<BookingGuestResponseDTO> getGuestsByBookingId(Long bookingId) {
        ensureBookingExists(bookingId);

        return bookingGuestRepository.findByBookingId(bookingId)
                .stream()
                .map(bookingGuestMapper::toResponseDTO)
                .toList();
    }

    @Override
    public BookingGuestResponseDTO getPrimaryGuest(Long bookingId) {
        ensureBookingExists(bookingId);

        BookingGuest primary = bookingGuestRepository.findByBookingIdAndPrimaryGuestTrue(bookingId)
                .orElseThrow(() -> new BookingGuestNotFoundException("No primary guest found for booking " + bookingId));

        return bookingGuestMapper.toResponseDTO(primary);
    }

    @Override
    public BookingGuestResponseDTO update(Long bookingId, Long guestId, BookingGuestRequestDTO requestDTO) {
        ensureBookingExists(bookingId);

        BookingGuest entity = bookingGuestRepository.findById(guestId)
                .orElseThrow(() -> new BookingGuestNotFoundException(guestId));

        // Verify guest belongs to the booking
        if (!entity.getBooking().getId().equals(bookingId)) {
            throw new BookingGuestNotFoundException("Guest " + guestId + " not found in booking " + bookingId);
        }

        // If this guest is being set as primary, unset any existing primary guest
        if (requestDTO.isPrimaryGuest() && !entity.isPrimaryGuest()) {
            bookingGuestRepository.findByBookingIdAndPrimaryGuestTrue(bookingId)
                    .ifPresent(existingPrimary -> {
                        if (!existingPrimary.getId().equals(guestId)) {
                            existingPrimary.setPrimaryGuest(false);
                            bookingGuestRepository.save(existingPrimary);
                        }
                    });
        }

        bookingGuestMapper.updateEntity(entity, requestDTO);
        BookingGuest saved = bookingGuestRepository.save(entity);

        return bookingGuestMapper.toResponseDTO(saved);
    }

    @Override
    public void delete(Long bookingId, Long guestId) {
        ensureBookingExists(bookingId);

        BookingGuest entity = bookingGuestRepository.findById(guestId)
                .orElseThrow(() -> new BookingGuestNotFoundException(guestId));

        // Verify guest belongs to the booking
        if (!entity.getBooking().getId().equals(bookingId)) {
            throw new BookingGuestNotFoundException("Guest " + guestId + " not found in booking " + bookingId);
        }

        bookingGuestRepository.delete(entity);
    }

    private void ensureBookingExists(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new BookingNotFoundException(bookingId);
        }
    }
}
