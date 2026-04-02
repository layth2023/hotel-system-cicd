package com.Review;

import com.Booking.Booking;
import com.Booking.BookingRepository;
import com.Booking.BookingStatus;
import com.Hotel.Hotel;
import com.Hotel.HotelRepository;
import com.Role.Role;
import com.Role.RoleRepository;
import com.Room.Room;
import com.Room.RoomRepository;
import com.RoomType.RoomType;
import com.RoomType.RoomTypeRepository;
import com.TestConfig;
import com.User.User;
import com.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Room Reviews functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class ReviewRoomTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User testUser;
    private Hotel testHotel;
    private Room testRoom;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        roomTypeRepository.deleteAll();
        hotelRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create role
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole = roleRepository.save(userRole);

        // Create user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.addRole(userRole);
        testUser = userRepository.save(testUser);

        // Create hotel
        testHotel = new Hotel();
        testHotel.setName("Test Hotel");
        testHotel.setAddress("123 Test Street");
        testHotel = hotelRepository.save(testHotel);

        // Create room type
        RoomType roomType = new RoomType();
        roomType.setName("Deluxe Suite");
        roomType.setCapacity(4);
        roomType.setBeds(2);
        roomType.setPricePerNight(new BigDecimal("150.00"));
        roomType.setCancellationRules("Free cancellation");
        roomType = roomTypeRepository.save(roomType);

        // Create room
        testRoom = new Room();
        testRoom.setRoomNumber("101");
        testRoom.setFloor(1);
        testRoom.setHotel(testHotel);
        testRoom.setRoomType(roomType);
        testRoom = roomRepository.save(testRoom);

        // Create booking
        testBooking = new Booking();
        testBooking.setUser(testUser);
        testBooking.setRoom(testRoom);
        testBooking.setCheckInDate(LocalDate.now().plusDays(1));
        testBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        testBooking.setNumberOfGuests(2);
        testBooking.setNumberOfAdults(2);
        testBooking.setPricePerNight(new BigDecimal("150.00"));
        testBooking.setTotalPrice(new BigDecimal("300.00"));
        testBooking.setStatus(BookingStatus.CONFIRMED);
        testBooking = bookingRepository.save(testBooking);
    }

    @Test
    @DisplayName("Should create review for room")
    void createReviewForRoom() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setRoomId(testRoom.getId());
        request.setRating(5);
        request.setTitle("Great Room!");
        request.setComment("The room was amazing, very clean and comfortable.");

        ReviewResponseDTO response = reviewService.create(request, testUser.getId());

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(testRoom.getId(), response.getRoomId());
        assertEquals("101", response.getRoomNumber());
        assertEquals(testHotel.getId(), response.getHotelId());
        assertEquals("Test Hotel", response.getHotelName());
        assertEquals(5, response.getRating());
    }

    @Test
    @DisplayName("Should create review for room with booking (verified)")
    void createVerifiedRoomReview() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setRoomId(testRoom.getId());
        request.setBookingId(testBooking.getId());
        request.setRating(5);
        request.setComment("Verified review with booking.");

        ReviewResponseDTO response = reviewService.create(request, testUser.getId());

        assertTrue(response.isVerified());
        assertEquals(testBooking.getId(), response.getBookingId());
    }

    @Test
    @DisplayName("Should set hotel automatically when only roomId is provided")
    void setHotelFromRoom() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setRoomId(testRoom.getId());
        request.setRating(4);
        request.setComment("Room review sets hotel automatically.");

        ReviewResponseDTO response = reviewService.create(request, testUser.getId());

        assertEquals(testHotel.getId(), response.getHotelId());
        assertEquals("Test Hotel", response.getHotelName());
    }

    @Test
    @DisplayName("Should get reviews by room")
    void getReviewsByRoom() {
        // Create and approve a review
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setRoomId(testRoom.getId());
        request.setRating(5);
        request.setComment("Test room review.");
        ReviewResponseDTO created = reviewService.create(request, testUser.getId());
        reviewService.approve(created.getId());

        Page<ReviewResponseDTO> reviews = reviewService.getByRoom(testRoom.getId(), PageRequest.of(0, 10));

        assertNotNull(reviews);
        assertEquals(1, reviews.getTotalElements());
        assertEquals(testRoom.getId(), reviews.getContent().get(0).getRoomId());
    }

    @Test
    @DisplayName("Should get average rating for room")
    void getAverageRatingForRoom() {
        // Create user 2 for second review
        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setEmail("test2@example.com");
        user2.setPassword("password123");
        user2 = userRepository.save(user2);

        // Create room 2 for second review (to avoid duplicate review error)
        Room room2 = new Room();
        room2.setRoomNumber("102");
        room2.setFloor(1);
        room2.setHotel(testHotel);
        room2.setRoomType(testRoom.getRoomType());
        room2 = roomRepository.save(room2);

        // Create first review (rating 5)
        ReviewRequestDTO request1 = new ReviewRequestDTO();
        request1.setRoomId(testRoom.getId());
        request1.setRating(5);
        request1.setComment("Excellent room!");
        ReviewResponseDTO review1 = reviewService.create(request1, testUser.getId());
        reviewService.approve(review1.getId());

        // Create second review for same room by different user (rating 3)
        ReviewRequestDTO request2 = new ReviewRequestDTO();
        request2.setRoomId(testRoom.getId());
        request2.setRating(3);
        request2.setComment("Average room.");
        ReviewResponseDTO review2 = reviewService.create(request2, user2.getId());
        reviewService.approve(review2.getId());

        Double avgRating = reviewService.getAverageRatingByRoom(testRoom.getId());

        assertNotNull(avgRating);
        assertEquals(4.0, avgRating, 0.01); // (5 + 3) / 2 = 4.0
    }

    @Test
    @DisplayName("Should include room details in review response")
    void includeRoomDetailsInResponse() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setRoomId(testRoom.getId());
        request.setRating(4);
        request.setComment("Nice room with good amenities.");

        ReviewResponseDTO response = reviewService.create(request, testUser.getId());

        assertEquals(testRoom.getId(), response.getRoomId());
        assertEquals("101", response.getRoomNumber());
    }

    @Test
    @DisplayName("Should throw exception for duplicate room review by same user")
    void throwExceptionForDuplicateRoomReview() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setRoomId(testRoom.getId());
        request.setRating(5);
        request.setComment("First review.");
        reviewService.create(request, testUser.getId());

        ReviewRequestDTO duplicateRequest = new ReviewRequestDTO();
        duplicateRequest.setRoomId(testRoom.getId());
        duplicateRequest.setRating(4);
        duplicateRequest.setComment("Duplicate review.");

        assertThrows(ReviewAlreadyExistsException.class, () -> {
            reviewService.create(duplicateRequest, testUser.getId());
        });
    }

    @Test
    @DisplayName("Should create review with both hotelId and roomId")
    void createReviewWithBothHotelAndRoom() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setHotelId(testHotel.getId());
        request.setRoomId(testRoom.getId());
        request.setRating(5);
        request.setComment("Review with both hotel and room.");

        ReviewResponseDTO response = reviewService.create(request, testUser.getId());

        assertEquals(testHotel.getId(), response.getHotelId());
        assertEquals(testRoom.getId(), response.getRoomId());
    }

    @Test
    @DisplayName("Review entity should have room field")
    void reviewEntityHasRoomField() {
        Review review = new Review();
        review.setUser(testUser);
        review.setHotel(testHotel);
        review.setRoom(testRoom);
        review.setRating(5);
        review.setComment("Test review.");

        Review saved = reviewRepository.save(review);

        assertNotNull(saved.getRoom());
        assertEquals(testRoom.getId(), saved.getRoom().getId());
    }

    @Test
    @DisplayName("Should include subcategory ratings in room review")
    void includeSubcategoryRatingsInRoomReview() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setRoomId(testRoom.getId());
        request.setRating(5);
        request.setComment("Detailed room review.");
        request.setCleanlinessRating(5);
        request.setServiceRating(4);
        request.setLocationRating(5);
        request.setValueRating(4);

        ReviewResponseDTO response = reviewService.create(request, testUser.getId());

        assertEquals(5, response.getCleanlinessRating());
        assertEquals(4, response.getServiceRating());
        assertEquals(5, response.getLocationRating());
        assertEquals(4, response.getValueRating());
    }
}
