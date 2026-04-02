package com.Booking;

import com.Hotel.Hotel;
import com.Notification.NotificationService;
import com.Room.Room;
import com.Room.RoomAmenityRepository;
import com.Room.RoomNotFoundException;
import com.Room.RoomRepository;
import com.RoomType.RoomType;
import com.User.User;
import com.User.UserNotFoundException;
import com.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomAmenityRepository roomAmenityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Hotel hotel;
    private RoomType roomType;
    private Room room;
    private Booking booking;
    private BookingRequestDTO requestDTO;
    private BookingResponseDTO responseDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        hotel.setAmenities(new HashSet<>());

        roomType = new RoomType();
        roomType.setId(1L);
        roomType.setName("Deluxe");
        roomType.setCapacity(4);
        roomType.setPricePerNight(new BigDecimal("150.00"));

        room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setFloor(1);
        room.setHotel(hotel);
        room.setRoomType(roomType);

        booking = new Booking();
        booking.setId(1L);
        booking.setConfirmationNumber("BK123456");
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.now().plusDays(1));
        booking.setCheckOutDate(LocalDate.now().plusDays(3));
        booking.setNumberOfGuests(2);
        booking.setNumberOfAdults(2);
        booking.setNumberOfChildren(0);
        booking.setPricePerNight(new BigDecimal("150.00"));
        booking.setTotalPrice(new BigDecimal("300.00"));
        booking.setStatus(BookingStatus.PENDING);

        requestDTO = new BookingRequestDTO();
        requestDTO.setRoomId(1L);
        requestDTO.setCheckInDate(LocalDate.now().plusDays(1));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(3));
        requestDTO.setNumberOfGuests(2);
        requestDTO.setNumberOfAdults(2);
        requestDTO.setNumberOfChildren(0);

        responseDTO = new BookingResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setConfirmationNumber("BK123456");
        responseDTO.setStatus(BookingStatus.PENDING);

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Create Booking Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create booking successfully")
        void shouldCreateBooking() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(bookingRepository.existsConflictingBooking(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(false);
            when(bookingMapper.toEntity(any(BookingRequestDTO.class), any(User.class), any(Room.class)))
                    .thenReturn(booking);
            when(roomAmenityRepository.calculateTotalAmenityPriceForRoom(1L))
                    .thenReturn(BigDecimal.ZERO);
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.toResponseDTO(any(Booking.class))).thenReturn(responseDTO);

            BookingResponseDTO result = bookingService.create(requestDTO, 1L);

            assertNotNull(result);
            assertEquals("BK123456", result.getConfirmationNumber());
            verify(notificationService).notifyBookingCreated(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> bookingService.create(requestDTO, 999L));
        }

        @Test
        @DisplayName("Should throw exception when room not found")
        void shouldThrowExceptionWhenRoomNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(roomRepository.findById(999L)).thenReturn(Optional.empty());
            requestDTO.setRoomId(999L);

            assertThrows(RoomNotFoundException.class, () -> bookingService.create(requestDTO, 1L));
        }

        @Test
        @DisplayName("Should throw exception when guests exceed capacity")
        void shouldThrowExceptionWhenGuestsExceedCapacity() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            requestDTO.setNumberOfGuests(10);

            assertThrows(BookingBadRequestException.class, () -> bookingService.create(requestDTO, 1L));
        }

        @Test
        @DisplayName("Should throw exception when room is not available")
        void shouldThrowExceptionWhenRoomNotAvailable() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(bookingRepository.existsConflictingBooking(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(true);

            assertThrows(BookingBadRequestException.class, () -> bookingService.create(requestDTO, 1L));
        }
    }

    @Nested
    @DisplayName("Get Booking Tests")
    class GetTests {

        @Test
        @DisplayName("Should get booking by id")
        void shouldGetById() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

            BookingResponseDTO result = bookingService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw exception when booking not found")
        void shouldThrowExceptionWhenNotFound() {
            when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(BookingNotFoundException.class, () -> bookingService.getById(999L));
        }

        @Test
        @DisplayName("Should get booking by confirmation number")
        void shouldGetByConfirmationNumber() {
            when(bookingRepository.findByConfirmationNumber("BK123456")).thenReturn(Optional.of(booking));
            when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

            BookingResponseDTO result = bookingService.getByConfirmationNumber("BK123456");

            assertNotNull(result);
            assertEquals("BK123456", result.getConfirmationNumber());
        }

        @Test
        @DisplayName("Should get bookings by user id")
        void shouldGetByUserId() {
            Page<Booking> bookingPage = new PageImpl<>(List.of(booking));
            when(bookingRepository.findByUserId(1L, pageable)).thenReturn(bookingPage);
            when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

            Page<BookingResponseDTO> result = bookingService.getByUserId(1L, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should get bookings by status")
        void shouldGetByStatus() {
            Page<Booking> bookingPage = new PageImpl<>(List.of(booking));
            when(bookingRepository.findByStatus(BookingStatus.PENDING, pageable)).thenReturn(bookingPage);
            when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

            Page<BookingResponseDTO> result = bookingService.getByStatus(BookingStatus.PENDING, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should get today check-ins")
        void shouldGetTodayCheckIns() {
            when(bookingRepository.findTodayCheckIns(any(LocalDate.class))).thenReturn(List.of(booking));
            when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

            List<BookingResponseDTO> result = bookingService.getTodayCheckIns();

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should get today check-outs")
        void shouldGetTodayCheckOuts() {
            when(bookingRepository.findTodayCheckOuts(any(LocalDate.class))).thenReturn(List.of(booking));
            when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

            List<BookingResponseDTO> result = bookingService.getTodayCheckOuts();

            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Update Booking Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update pending booking")
        void shouldUpdatePendingBooking() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.toResponseDTO(any(Booking.class))).thenReturn(responseDTO);

            BookingResponseDTO result = bookingService.update(1L, requestDTO);

            assertNotNull(result);
            verify(bookingMapper).updateEntity(any(Booking.class), any(BookingRequestDTO.class));
        }

        @Test
        @DisplayName("Should update confirmed booking")
        void shouldUpdateConfirmedBooking() {
            booking.setStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.toResponseDTO(any(Booking.class))).thenReturn(responseDTO);

            BookingResponseDTO result = bookingService.update(1L, requestDTO);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should throw exception when updating checked-in booking")
        void shouldThrowExceptionWhenUpdatingCheckedIn() {
            booking.setStatus(BookingStatus.CHECKED_IN);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            assertThrows(BookingBadRequestException.class, () -> bookingService.update(1L, requestDTO));
        }

        @Test
        @DisplayName("Should throw exception when updating cancelled booking")
        void shouldThrowExceptionWhenUpdatingCancelled() {
            booking.setStatus(BookingStatus.CANCELLED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            assertThrows(BookingBadRequestException.class, () -> bookingService.update(1L, requestDTO));
        }
    }

    @Nested
    @DisplayName("Cancel Booking Tests")
    class CancelTests {

        @Test
        @DisplayName("Should cancel pending booking")
        void shouldCancelPendingBooking() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.toResponseDTO(any(Booking.class))).thenReturn(responseDTO);

            BookingResponseDTO result = bookingService.cancel(1L, "Guest request");

            assertNotNull(result);
            verify(notificationService).notifyBookingCancelled(any(Booking.class));
        }

        @Test
        @DisplayName("Should cancel confirmed booking")
        void shouldCancelConfirmedBooking() {
            booking.setStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.toResponseDTO(any(Booking.class))).thenReturn(responseDTO);

            BookingResponseDTO result = bookingService.cancel(1L, "Guest request");

            assertNotNull(result);
            assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when cancelling checked-in booking")
        void shouldThrowExceptionWhenCancellingCheckedIn() {
            booking.setStatus(BookingStatus.CHECKED_IN);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            assertThrows(BookingBadRequestException.class, () -> bookingService.cancel(1L, "reason"));
        }
    }

    @Nested
    @DisplayName("Status Change Tests")
    class StatusChangeTests {

        @Test
        @DisplayName("Should check-in confirmed booking")
        void shouldCheckInConfirmedBooking() {
            booking.setStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.toResponseDTO(any(Booking.class))).thenReturn(responseDTO);

            BookingResponseDTO result = bookingService.checkIn(1L);

            assertNotNull(result);
            assertEquals(BookingStatus.CHECKED_IN, booking.getStatus());
            assertNotNull(booking.getActualCheckIn());
            verify(notificationService).notifyBookingCheckedIn(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when checking in non-confirmed booking")
        void shouldThrowExceptionWhenCheckingInNonConfirmed() {
            booking.setStatus(BookingStatus.PENDING);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            assertThrows(BookingBadRequestException.class, () -> bookingService.checkIn(1L));
        }

        @Test
        @DisplayName("Should check-out checked-in booking")
        void shouldCheckOutCheckedInBooking() {
            booking.setStatus(BookingStatus.CHECKED_IN);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.toResponseDTO(any(Booking.class))).thenReturn(responseDTO);

            BookingResponseDTO result = bookingService.checkOut(1L);

            assertNotNull(result);
            assertEquals(BookingStatus.CHECKED_OUT, booking.getStatus());
            assertNotNull(booking.getActualCheckOut());
            verify(notificationService).notifyBookingCheckedOut(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when checking out non-checked-in booking")
        void shouldThrowExceptionWhenCheckingOutNonCheckedIn() {
            booking.setStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            assertThrows(BookingBadRequestException.class, () -> bookingService.checkOut(1L));
        }

        @Test
        @DisplayName("Should mark no-show for confirmed booking")
        void shouldMarkNoShowForConfirmedBooking() {
            booking.setStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.toResponseDTO(any(Booking.class))).thenReturn(responseDTO);

            BookingResponseDTO result = bookingService.markNoShow(1L);

            assertNotNull(result);
            assertEquals(BookingStatus.NO_SHOW, booking.getStatus());
        }

        @Test
        @DisplayName("Should confirm pending booking")
        void shouldConfirmPendingBooking() {
            booking.setStatus(BookingStatus.PENDING);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
            when(bookingMapper.toResponseDTO(any(Booking.class))).thenReturn(responseDTO);

            BookingResponseDTO result = bookingService.confirm(1L);

            assertNotNull(result);
            assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
            verify(notificationService).notifyBookingConfirmed(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when confirming non-pending booking")
        void shouldThrowExceptionWhenConfirmingNonPending() {
            booking.setStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            assertThrows(BookingBadRequestException.class, () -> bookingService.confirm(1L));
        }
    }

    @Nested
    @DisplayName("Delete Booking Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete booking")
        void shouldDeleteBooking() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            bookingService.delete(1L);

            verify(bookingRepository).delete(booking);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent booking")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(BookingNotFoundException.class, () -> bookingService.delete(999L));
        }
    }
}
