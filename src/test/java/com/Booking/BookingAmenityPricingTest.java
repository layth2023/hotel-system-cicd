package com.Booking;

import com.Amenity.Amenity;
import com.Amenity.AmenityRepository;
import com.Hotel.Hotel;
import com.Hotel.HotelRepository;
import com.Role.Role;
import com.Role.RoleRepository;
import com.Room.Room;
import com.Room.RoomAmenity;
import com.Room.RoomAmenityRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Booking with Amenity Pricing.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class BookingAmenityPricingTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomAmenityRepository roomAmenityRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private Room testRoom;
    private Amenity chairAmenity;
    private Amenity wifiAmenity;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        roomAmenityRepository.deleteAll();
        roomRepository.deleteAll();
        amenityRepository.deleteAll();
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
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setAddress("123 Test Street");
        hotel = hotelRepository.save(hotel);

        // Create room type with price $100/night
        RoomType roomType = new RoomType();
        roomType.setName("Standard Room");
        roomType.setCapacity(2);
        roomType.setBeds(1);
        roomType.setPricePerNight(new BigDecimal("100.00"));
        roomType.setCancellationRules("Free cancellation");
        roomType = roomTypeRepository.save(roomType);

        // Create room
        testRoom = new Room();
        testRoom.setRoomNumber("101");
        testRoom.setFloor(1);
        testRoom.setHotel(hotel);
        testRoom.setRoomType(roomType);
        testRoom = roomRepository.save(testRoom);

        // Create amenities
        chairAmenity = new Amenity();
        chairAmenity.setName("Chair");
        chairAmenity.setDescription("Comfortable chair");
        chairAmenity = amenityRepository.save(chairAmenity);

        wifiAmenity = new Amenity();
        wifiAmenity.setName("WiFi");
        wifiAmenity.setDescription("High-speed internet");
        wifiAmenity = amenityRepository.save(wifiAmenity);
    }

    @Test
    @DisplayName("Should include amenity total in booking price calculation")
    void includeAmenityTotalInBookingPrice() {
        // Add amenities to room: 2 chairs @ $10 = $20/day
        RoomAmenity roomAmenity = new RoomAmenity();
        roomAmenity.setRoom(testRoom);
        roomAmenity.setAmenity(chairAmenity);
        roomAmenity.setQuantity(2);
        roomAmenity.setPricePerUnit(new BigDecimal("10.00"));
        roomAmenityRepository.save(roomAmenity);

        // Create booking for 2 nights
        BookingRequestDTO request = new BookingRequestDTO();
        request.setRoomId(testRoom.getId());
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(3)); // 2 nights
        request.setNumberOfGuests(2);
        request.setNumberOfAdults(2);

        BookingResponseDTO response = bookingService.create(request, testUser.getId());

        // Expected: Room $100/night * 2 + Amenity $20/night * 2 = $200 + $40 = $240
        assertNotNull(response);
        assertEquals(new BigDecimal("20.00"), response.getAmenityTotal());
        assertEquals(new BigDecimal("240.00"), response.getTotalPrice());
    }

    @Test
    @DisplayName("Should calculate correct total with multiple amenities")
    void calculateTotalWithMultipleAmenities() {
        // Add chair: 2 @ $10 = $20/day
        RoomAmenity chairRoomAmenity = new RoomAmenity();
        chairRoomAmenity.setRoom(testRoom);
        chairRoomAmenity.setAmenity(chairAmenity);
        chairRoomAmenity.setQuantity(2);
        chairRoomAmenity.setPricePerUnit(new BigDecimal("10.00"));
        roomAmenityRepository.save(chairRoomAmenity);

        // Add wifi: 1 @ $5 = $5/day
        RoomAmenity wifiRoomAmenity = new RoomAmenity();
        wifiRoomAmenity.setRoom(testRoom);
        wifiRoomAmenity.setAmenity(wifiAmenity);
        wifiRoomAmenity.setQuantity(1);
        wifiRoomAmenity.setPricePerUnit(new BigDecimal("5.00"));
        roomAmenityRepository.save(wifiRoomAmenity);

        // Create booking for 3 nights
        BookingRequestDTO request = new BookingRequestDTO();
        request.setRoomId(testRoom.getId());
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(4)); // 3 nights
        request.setNumberOfGuests(2);
        request.setNumberOfAdults(2);

        BookingResponseDTO response = bookingService.create(request, testUser.getId());

        // Amenity total per day: $20 + $5 = $25
        // Total: Room $100 * 3 + Amenity $25 * 3 = $300 + $75 = $375
        assertEquals(new BigDecimal("25.00"), response.getAmenityTotal());
        assertEquals(new BigDecimal("375.00"), response.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle booking with free amenities")
    void handleBookingWithFreeAmenities() {
        // Add free WiFi
        RoomAmenity freeWifi = new RoomAmenity();
        freeWifi.setRoom(testRoom);
        freeWifi.setAmenity(wifiAmenity);
        freeWifi.setQuantity(1);
        freeWifi.setPricePerUnit(BigDecimal.ZERO);
        roomAmenityRepository.save(freeWifi);

        // Create booking for 2 nights
        BookingRequestDTO request = new BookingRequestDTO();
        request.setRoomId(testRoom.getId());
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(3)); // 2 nights
        request.setNumberOfGuests(2);
        request.setNumberOfAdults(2);

        BookingResponseDTO response = bookingService.create(request, testUser.getId());

        // Amenity is free, so total = room price only
        assertEquals(0, BigDecimal.ZERO.compareTo(response.getAmenityTotal()));
        assertEquals(new BigDecimal("200.00"), response.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle booking with no amenities")
    void handleBookingWithNoAmenities() {
        // No amenities added to room

        // Create booking for 2 nights
        BookingRequestDTO request = new BookingRequestDTO();
        request.setRoomId(testRoom.getId());
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(3)); // 2 nights
        request.setNumberOfGuests(2);
        request.setNumberOfAdults(2);

        BookingResponseDTO response = bookingService.create(request, testUser.getId());

        // No amenities, so total = room price only
        assertEquals(0, BigDecimal.ZERO.compareTo(response.getAmenityTotal()));
        assertEquals(new BigDecimal("200.00"), response.getTotalPrice());
    }

    @Test
    @DisplayName("Booking entity should have amenityTotal field")
    void bookingEntityHasAmenityTotalField() {
        Booking booking = new Booking();
        booking.setUser(testUser);
        booking.setRoom(testRoom);
        booking.setCheckInDate(LocalDate.now().plusDays(1));
        booking.setCheckOutDate(LocalDate.now().plusDays(3));
        booking.setNumberOfGuests(2);
        booking.setNumberOfAdults(2);
        booking.setPricePerNight(new BigDecimal("100.00"));
        booking.setAmenityTotal(new BigDecimal("25.00"));
        booking.setTotalPrice(booking.calculateTotalPrice());

        Booking saved = bookingRepository.save(booking);

        assertNotNull(saved.getAmenityTotal());
        assertEquals(new BigDecimal("25.00"), saved.getAmenityTotal());
    }

    @Test
    @DisplayName("Should calculate total price with amenity in Booking entity")
    void calculateTotalPriceWithAmenityInEntity() {
        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.now().plusDays(1));
        booking.setCheckOutDate(LocalDate.now().plusDays(4)); // 3 nights
        booking.setPricePerNight(new BigDecimal("100.00"));
        booking.setAmenityTotal(new BigDecimal("20.00"));

        BigDecimal totalPrice = booking.calculateTotalPrice();

        // (100 + 20) * 3 = 360
        assertEquals(new BigDecimal("360.00"), totalPrice);
    }

    @Test
    @DisplayName("Should handle single night booking with amenities")
    void handleSingleNightBookingWithAmenities() {
        // Add amenity: 1 chair @ $15 = $15/day
        RoomAmenity roomAmenity = new RoomAmenity();
        roomAmenity.setRoom(testRoom);
        roomAmenity.setAmenity(chairAmenity);
        roomAmenity.setQuantity(1);
        roomAmenity.setPricePerUnit(new BigDecimal("15.00"));
        roomAmenityRepository.save(roomAmenity);

        // Create booking for 1 night
        BookingRequestDTO request = new BookingRequestDTO();
        request.setRoomId(testRoom.getId());
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(2)); // 1 night
        request.setNumberOfGuests(1);
        request.setNumberOfAdults(1);

        BookingResponseDTO response = bookingService.create(request, testUser.getId());

        // Room $100 + Amenity $15 = $115 for 1 night
        assertEquals(new BigDecimal("15.00"), response.getAmenityTotal());
        assertEquals(new BigDecimal("115.00"), response.getTotalPrice());
    }
}
