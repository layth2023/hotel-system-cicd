package com.config;

import com.Amenity.Amenity;
import com.Amenity.AmenityRequestDTO;
import com.Booking.Booking;
import com.Booking.BookingRequestDTO;
import com.Booking.BookingStatus;
import com.Hotel.Hotel;
import com.Hotel.HotelRequestDTO;
import com.Payment.Payment;
import com.Payment.PaymentMethod;
import com.Payment.PaymentRequestDTO;
import com.Payment.PaymentStatus;
import com.Review.Review;
import com.Review.ReviewRequestDTO;
import com.Role.Role;
import com.Room.Room;
import com.Room.RoomRequestDTO;
import com.RoomType.RoomType;
import com.RoomType.RoomTypeRequestDTO;
import com.User.User;
import com.User.UserRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;

public class TestDataFactory {

    public static Hotel createHotel() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        hotel.setAddress("123 Test Street");
        hotel.setCity("Test City");
        hotel.setCountry("Test Country");
        hotel.setPhone("+1234567890");
        hotel.setEmail("test@hotel.com");
        hotel.setStarRating(4);
        hotel.setNumberOfFloors(10);
        hotel.setAmenities(new HashSet<>());
        return hotel;
    }

    public static HotelRequestDTO createHotelRequestDTO() {
        HotelRequestDTO dto = new HotelRequestDTO();
        dto.setName("Test Hotel");
        dto.setAddress("123 Test Street");
        dto.setCity("Test City");
        dto.setCountry("Test Country");
        dto.setPhone("+1234567890");
        dto.setEmail("test@hotel.com");
        dto.setNumberOfFloors(10);
        return dto;
    }

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword123!");
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setRoles(new HashSet<>());
        return user;
    }

    public static UserRequestDTO createUserRequestDTO() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("TestPassword123!");
        return dto;
    }

    public static Role createRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        role.setDescription("Standard user role");
        return role;
    }

    public static Role createAdminRole() {
        Role role = new Role();
        role.setId(2L);
        role.setName("ROLE_ADMIN");
        role.setDescription("Administrator role");
        return role;
    }

    public static RoomType createRoomType() {
        RoomType roomType = new RoomType();
        roomType.setId(1L);
        roomType.setName("Deluxe Suite");
        roomType.setDescription("Luxurious suite with city view");
        roomType.setCapacity(4);
        roomType.setBeds(2);
        roomType.setPricePerNight(new BigDecimal("150.00"));
        roomType.setCancellationRules("Free cancellation up to 24 hours before check-in");
        return roomType;
    }

    public static RoomTypeRequestDTO createRoomTypeRequestDTO() {
        RoomTypeRequestDTO dto = new RoomTypeRequestDTO();
        dto.setName("Deluxe Suite");
        dto.setDescription("Luxurious suite with city view");
        dto.setCapacity(4);
        dto.setBeds(2);
        dto.setPricePerNight(new BigDecimal("150.00"));
        dto.setCancellationRules("Free cancellation up to 24 hours before check-in");
        return dto;
    }

    public static Room createRoom(Hotel hotel, RoomType roomType) {
        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setFloor(1);
        room.setHotel(hotel);
        room.setRoomType(roomType);
        return room;
    }

    public static RoomRequestDTO createRoomRequestDTO() {
        RoomRequestDTO dto = new RoomRequestDTO();
        dto.setRoomNumber("101");
        dto.setFloor(1);
        dto.setHotelId(1L);
        dto.setRoomTypeId(1L);
        return dto;
    }

    public static Amenity createAmenity() {
        Amenity amenity = new Amenity();
        amenity.setId(1L);
        amenity.setName("WiFi");
        amenity.setDescription("High-speed wireless internet");
        amenity.setActive(true);
        amenity.setHotels(new HashSet<>());
        return amenity;
    }

    public static AmenityRequestDTO createAmenityRequestDTO() {
        AmenityRequestDTO dto = new AmenityRequestDTO();
        dto.setName("WiFi");
        dto.setDescription("High-speed wireless internet");
        return dto;
    }

    public static Booking createBooking(User user, Room room) {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setConfirmationNumber("BK123456789");
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
        return booking;
    }

    public static BookingRequestDTO createBookingRequestDTO() {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setRoomId(1L);
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(3));
        dto.setNumberOfGuests(2);
        dto.setNumberOfAdults(2);
        dto.setNumberOfChildren(0);
        dto.setSpecialRequests("Late check-in");
        return dto;
    }

    public static Payment createPayment(Booking booking) {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setBooking(booking);
        payment.setAmount(new BigDecimal("300.00"));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        return payment;
    }

    public static PaymentRequestDTO createPaymentRequestDTO() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setBookingId(1L);
        dto.setAmount(new BigDecimal("300.00"));
        return dto;
    }

    public static Review createReview(User user, Hotel hotel) {
        Review review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setHotel(hotel);
        review.setRating(5);
        review.setTitle("Great Stay");
        review.setComment("Excellent service and clean rooms");
        review.setApproved(true);
        review.setVerified(false);
        return review;
    }

    public static ReviewRequestDTO createReviewRequestDTO() {
        ReviewRequestDTO dto = new ReviewRequestDTO();
        dto.setHotelId(1L);
        dto.setRating(5);
        dto.setTitle("Great Stay");
        dto.setComment("Excellent service and clean rooms");
        return dto;
    }
}
