package com.Review;

import com.Booking.Booking;
import com.Booking.BookingNotFoundException;
import com.Booking.BookingRepository;
import com.Hotel.Hotel;
import com.Hotel.HotelNotFoundException;
import com.Hotel.HotelRepository;
import com.Notification.NotificationService;
import com.Room.Room;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User user;
    private Hotel hotel;
    private Room room;
    private Booking booking;
    private Review review;
    private ReviewRequestDTO requestDTO;
    private ReviewResponseDTO responseDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        hotel.setAmenities(new HashSet<>());

        RoomType roomType = new RoomType();
        roomType.setId(1L);
        roomType.setCapacity(4);
        roomType.setPricePerNight(new BigDecimal("150.00"));

        room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setHotel(hotel);
        room.setRoomType(roomType);

        booking = new Booking();
        booking.setId(1L);

        review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setHotel(hotel);
        review.setRating(5);
        review.setTitle("Great Stay");
        review.setComment("Excellent service");
        review.setApproved(true);

        requestDTO = new ReviewRequestDTO();
        requestDTO.setHotelId(1L);
        requestDTO.setRating(5);
        requestDTO.setTitle("Great Stay");
        requestDTO.setComment("Excellent service");

        responseDTO = new ReviewResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setRating(5);
        responseDTO.setTitle("Great Stay");
        responseDTO.setComment("Excellent service");

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Create Review Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create hotel review successfully")
        void shouldCreateHotelReview() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
            when(reviewRepository.existsByUserIdAndHotelId(1L, 1L)).thenReturn(false);
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(reviewMapper.toResponseDTO(any(Review.class))).thenReturn(responseDTO);

            ReviewResponseDTO result = reviewService.create(requestDTO, 1L);

            assertNotNull(result);
            assertEquals(5, result.getRating());
        }

        @Test
        @DisplayName("Should create room review successfully")
        void shouldCreateRoomReview() {
            requestDTO.setHotelId(null);
            requestDTO.setRoomId(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(reviewRepository.existsByUserIdAndRoomId(1L, 1L)).thenReturn(false);
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(reviewMapper.toResponseDTO(any(Review.class))).thenReturn(responseDTO);

            ReviewResponseDTO result = reviewService.create(requestDTO, 1L);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should create verified review with booking")
        void shouldCreateVerifiedReviewWithBooking() {
            requestDTO.setBookingId(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
            when(reviewRepository.existsByUserIdAndHotelId(1L, 1L)).thenReturn(false);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(reviewMapper.toResponseDTO(any(Review.class))).thenReturn(responseDTO);

            reviewService.create(requestDTO, 1L);

            verify(bookingRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> reviewService.create(requestDTO, 999L));
        }

        @Test
        @DisplayName("Should throw exception when neither hotel nor room provided")
        void shouldThrowExceptionWhenNeitherProvided() {
            requestDTO.setHotelId(null);
            requestDTO.setRoomId(null);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            assertThrows(IllegalArgumentException.class, () -> reviewService.create(requestDTO, 1L));
        }

        @Test
        @DisplayName("Should throw exception when hotel not found")
        void shouldThrowExceptionWhenHotelNotFound() {
            requestDTO.setHotelId(999L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(HotelNotFoundException.class, () -> reviewService.create(requestDTO, 1L));
        }

        @Test
        @DisplayName("Should throw exception when room not found")
        void shouldThrowExceptionWhenRoomNotFound() {
            requestDTO.setHotelId(null);
            requestDTO.setRoomId(999L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(roomRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RoomNotFoundException.class, () -> reviewService.create(requestDTO, 1L));
        }

        @Test
        @DisplayName("Should throw exception when user already reviewed hotel")
        void shouldThrowExceptionWhenAlreadyReviewedHotel() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
            when(reviewRepository.existsByUserIdAndHotelId(1L, 1L)).thenReturn(true);

            assertThrows(ReviewAlreadyExistsException.class, () -> reviewService.create(requestDTO, 1L));
        }

        @Test
        @DisplayName("Should throw exception when booking not found")
        void shouldThrowExceptionWhenBookingNotFound() {
            requestDTO.setBookingId(999L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
            when(reviewRepository.existsByUserIdAndHotelId(1L, 1L)).thenReturn(false);
            when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(BookingNotFoundException.class, () -> reviewService.create(requestDTO, 1L));
        }
    }

    @Nested
    @DisplayName("Get Review Tests")
    class GetTests {

        @Test
        @DisplayName("Should get review by id")
        void shouldGetById() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
            when(reviewMapper.toResponseDTO(review)).thenReturn(responseDTO);

            ReviewResponseDTO result = reviewService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw exception when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ReviewNotFoundException.class, () -> reviewService.getById(999L));
        }

        @Test
        @DisplayName("Should get reviews by hotel")
        void shouldGetByHotel() {
            Page<Review> reviewPage = new PageImpl<>(List.of(review));
            when(reviewRepository.findByHotelIdAndApprovedTrue(1L, pageable)).thenReturn(reviewPage);
            when(reviewMapper.toResponseDTO(review)).thenReturn(responseDTO);

            Page<ReviewResponseDTO> result = reviewService.getByHotel(1L, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should get reviews by user")
        void shouldGetByUser() {
            when(reviewRepository.findByUserId(1L)).thenReturn(List.of(review));
            when(reviewMapper.toResponseDTO(review)).thenReturn(responseDTO);

            List<ReviewResponseDTO> result = reviewService.getByUser(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should get reviews by room")
        void shouldGetByRoom() {
            Page<Review> reviewPage = new PageImpl<>(List.of(review));
            when(reviewRepository.findByRoomIdAndApprovedTrue(1L, pageable)).thenReturn(reviewPage);
            when(reviewMapper.toResponseDTO(review)).thenReturn(responseDTO);

            Page<ReviewResponseDTO> result = reviewService.getByRoom(1L, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should get pending reviews")
        void shouldGetPendingReviews() {
            review.setApproved(false);
            Page<Review> reviewPage = new PageImpl<>(List.of(review));
            when(reviewRepository.findByApprovedFalse(pageable)).thenReturn(reviewPage);
            when(reviewMapper.toResponseDTO(review)).thenReturn(responseDTO);

            Page<ReviewResponseDTO> result = reviewService.getPendingReviews(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Update Review Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update review by owner")
        void shouldUpdateByOwner() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(reviewMapper.toResponseDTO(any(Review.class))).thenReturn(responseDTO);

            ReviewResponseDTO result = reviewService.update(1L, requestDTO, 1L);

            assertNotNull(result);
            verify(reviewMapper).updateEntity(any(Review.class), any(ReviewRequestDTO.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-owner")
        void shouldThrowExceptionWhenUpdatingNonOwner() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            assertThrows(SecurityException.class, () -> reviewService.update(1L, requestDTO, 999L));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent")
        void shouldThrowExceptionWhenUpdatingNonExistent() {
            when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ReviewNotFoundException.class, () -> reviewService.update(999L, requestDTO, 1L));
        }
    }

    @Nested
    @DisplayName("Delete Review Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete review by owner")
        void shouldDeleteByOwner() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            reviewService.delete(1L, 1L);

            verify(reviewRepository).delete(review);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-owner")
        void shouldThrowExceptionWhenDeletingNonOwner() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            assertThrows(SecurityException.class, () -> reviewService.delete(1L, 999L));
        }
    }

    @Nested
    @DisplayName("Approve Review Tests")
    class ApproveTests {

        @Test
        @DisplayName("Should approve review")
        void shouldApproveReview() {
            review.setApproved(false);
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(reviewMapper.toResponseDTO(any(Review.class))).thenReturn(responseDTO);

            ReviewResponseDTO result = reviewService.approve(1L);

            assertNotNull(result);
            assertTrue(review.isApproved());
            verify(notificationService).notifyReviewApproved(any(Review.class));
        }
    }

    @Nested
    @DisplayName("Response Tests")
    class ResponseTests {

        @Test
        @DisplayName("Should add response to review")
        void shouldAddResponse() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(reviewMapper.toResponseDTO(any(Review.class))).thenReturn(responseDTO);

            ReviewResponseDTO result = reviewService.addResponse(1L, "Thank you for your feedback!");

            assertNotNull(result);
            assertNotNull(review.getResponse());
            assertNotNull(review.getResponseDate());
            verify(notificationService).notifyReviewResponded(any(Review.class));
        }
    }

    @Nested
    @DisplayName("Rating Tests")
    class RatingTests {

        @Test
        @DisplayName("Should get average rating by hotel")
        void shouldGetAverageRatingByHotel() {
            when(reviewRepository.getAverageRatingByHotelId(1L)).thenReturn(4.5);

            Double result = reviewService.getAverageRating(1L);

            assertEquals(4.5, result);
        }

        @Test
        @DisplayName("Should get average rating by room")
        void shouldGetAverageRatingByRoom() {
            when(reviewRepository.getAverageRatingByRoomId(1L)).thenReturn(4.2);

            Double result = reviewService.getAverageRatingByRoom(1L);

            assertEquals(4.2, result);
        }
    }
}
