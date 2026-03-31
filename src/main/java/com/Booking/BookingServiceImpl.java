package com.Booking;

import com.Notification.NotificationService;
import com.Room.Room;
import com.Room.RoomAmenityRepository;
import com.Room.RoomNotFoundException;
import com.Room.RoomRepository;
import com.User.User;
import com.User.UserNotFoundException;
import com.User.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final RoomRepository roomRepository;
    private final RoomAmenityRepository roomAmenityRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              BookingMapper bookingMapper,
                              RoomRepository roomRepository,
                              RoomAmenityRepository roomAmenityRepository,
                              UserRepository userRepository,
                              NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.roomRepository = roomRepository;
        this.roomAmenityRepository = roomAmenityRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public BookingResponseDTO create(BookingRequestDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Room room = roomRepository.findById(requestDTO.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(requestDTO.getRoomId()));

        // Validate room capacity
        if (requestDTO.getNumberOfGuests() > room.getRoomType().getCapacity()) {
            throw new BookingBadRequestException(
                    "Number of guests (" + requestDTO.getNumberOfGuests() +
                    ") exceeds room capacity (" + room.getRoomType().getCapacity() + ")");
        }

        // Check for conflicting bookings
        if (bookingRepository.existsConflictingBooking(
                requestDTO.getRoomId(),
                requestDTO.getCheckInDate(),
                requestDTO.getCheckOutDate())) {
            throw new BookingBadRequestException("Room is not available for the selected dates");
        }

        Booking entity = bookingMapper.toEntity(requestDTO, user, room);

        // Calculate and set amenity total for the room
        java.math.BigDecimal amenityTotal = roomAmenityRepository.calculateTotalAmenityPriceForRoom(room.getId());
        entity.setAmenityTotal(amenityTotal);
        entity.setTotalPrice(entity.calculateTotalPrice());

        Booking saved = bookingRepository.save(entity);

        // Send notification
        notificationService.notifyBookingCreated(saved);

        return bookingMapper.toResponseDTO(saved);
    }

    @Override
    public BookingResponseDTO getById(Long id) {
        Booking entity = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        return bookingMapper.toResponseDTO(entity);
    }

    @Override
    public BookingResponseDTO getByConfirmationNumber(String confirmationNumber) {
        Booking entity = bookingRepository.findByConfirmationNumber(confirmationNumber)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with confirmation number: " + confirmationNumber));
        return bookingMapper.toResponseDTO(entity);
    }

    @Override
    public BookingResponseDTO update(Long id, BookingRequestDTO requestDTO) {
        Booking entity = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        // Can only update PENDING or CONFIRMED bookings
        if (entity.getStatus() != BookingStatus.PENDING && entity.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingBadRequestException("Cannot update booking with status: " + entity.getStatus());
        }

        // Check for conflicting bookings if dates changed
        if (!entity.getCheckInDate().equals(requestDTO.getCheckInDate()) ||
            !entity.getCheckOutDate().equals(requestDTO.getCheckOutDate())) {

            List<Booking> conflicts = bookingRepository.findConflictingBookings(
                    entity.getRoom().getId(),
                    requestDTO.getCheckInDate(),
                    requestDTO.getCheckOutDate());

            // Exclude current booking from conflicts
            conflicts.removeIf(b -> b.getId().equals(id));

            if (!conflicts.isEmpty()) {
                throw new BookingBadRequestException("Room is not available for the selected dates");
            }
        }

        bookingMapper.updateEntity(entity, requestDTO);
        Booking saved = bookingRepository.save(entity);

        return bookingMapper.toResponseDTO(saved);
    }

    @Override
    public void delete(Long id) {
        Booking entity = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        bookingRepository.delete(entity);
    }

    @Override
    public Page<BookingResponseDTO> getByUserId(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable)
                .map(bookingMapper::toResponseDTO);
    }

    @Override
    public Page<BookingResponseDTO> getUpcomingByUserId(Long userId, Pageable pageable) {
        return bookingRepository.findUpcomingByUserId(userId, LocalDate.now(), pageable)
                .map(bookingMapper::toResponseDTO);
    }

    @Override
    public Page<BookingResponseDTO> getByRoomId(Long roomId, Pageable pageable) {
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
        return bookingRepository.findByRoomId(roomId, pageable)
                .map(bookingMapper::toResponseDTO);
    }

    @Override
    public Page<BookingResponseDTO> getByHotelId(Long hotelId, Pageable pageable) {
        return bookingRepository.findByHotelId(hotelId, pageable)
                .map(bookingMapper::toResponseDTO);
    }

    @Override
    public Page<BookingResponseDTO> getByStatus(BookingStatus status, Pageable pageable) {
        return bookingRepository.findByStatus(status, pageable)
                .map(bookingMapper::toResponseDTO);
    }

    @Override
    public List<BookingResponseDTO> getTodayCheckIns() {
        return bookingRepository.findTodayCheckIns(LocalDate.now())
                .stream()
                .map(bookingMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<BookingResponseDTO> getTodayCheckOuts() {
        return bookingRepository.findTodayCheckOuts(LocalDate.now())
                .stream()
                .map(bookingMapper::toResponseDTO)
                .toList();
    }

    @Override
    public BookingResponseDTO cancel(Long id, String reason) {
        Booking entity = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (!entity.isCancellable()) {
            throw new BookingBadRequestException("Booking cannot be cancelled. Current status: " + entity.getStatus());
        }

        entity.setStatus(BookingStatus.CANCELLED);
        entity.setCancellationDate(LocalDateTime.now());
        entity.setCancellationReason(reason);

        Booking saved = bookingRepository.save(entity);

        // Send notification
        notificationService.notifyBookingCancelled(saved);

        return bookingMapper.toResponseDTO(saved);
    }

    @Override
    public BookingResponseDTO checkIn(Long id) {
        Booking entity = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (entity.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingBadRequestException("Only CONFIRMED bookings can be checked in. Current status: " + entity.getStatus());
        }

        entity.setStatus(BookingStatus.CHECKED_IN);
        entity.setActualCheckIn(LocalDateTime.now());

        Booking saved = bookingRepository.save(entity);

        // Send notification
        notificationService.notifyBookingCheckedIn(saved);

        return bookingMapper.toResponseDTO(saved);
    }

    @Override
    public BookingResponseDTO checkOut(Long id) {
        Booking entity = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (entity.getStatus() != BookingStatus.CHECKED_IN) {
            throw new BookingBadRequestException("Only CHECKED_IN bookings can be checked out. Current status: " + entity.getStatus());
        }

        entity.setStatus(BookingStatus.CHECKED_OUT);
        entity.setActualCheckOut(LocalDateTime.now());

        Booking saved = bookingRepository.save(entity);

        // Send notification
        notificationService.notifyBookingCheckedOut(saved);

        return bookingMapper.toResponseDTO(saved);
    }

    @Override
    public BookingResponseDTO markNoShow(Long id) {
        Booking entity = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (entity.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingBadRequestException("Only CONFIRMED bookings can be marked as no-show. Current status: " + entity.getStatus());
        }

        entity.setStatus(BookingStatus.NO_SHOW);

        Booking saved = bookingRepository.save(entity);
        return bookingMapper.toResponseDTO(saved);
    }

    @Override
    public BookingResponseDTO confirm(Long id) {
        Booking entity = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (entity.getStatus() != BookingStatus.PENDING) {
            throw new BookingBadRequestException("Only PENDING bookings can be confirmed. Current status: " + entity.getStatus());
        }

        entity.setStatus(BookingStatus.CONFIRMED);

        Booking saved = bookingRepository.save(entity);

        // Send notification
        notificationService.notifyBookingConfirmed(saved);

        return bookingMapper.toResponseDTO(saved);
    }
}
