package com.Room;

import com.Amenity.Amenity;
import com.Amenity.AmenityNotFoundException;
import com.Amenity.AmenityRepository;
import com.Hotel.Hotel;
import com.Hotel.HotelRepository;
import com.RoomType.RoomType;
import com.RoomType.RoomTypeRepository;
import com.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for RoomAmenity service - amenities with quantity and pricing.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class RoomAmenityServiceTest {

    @Autowired
    private RoomAmenityService roomAmenityService;

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

    private Room testRoom;
    private Amenity testAmenity1;
    private Amenity testAmenity2;

    @BeforeEach
    void setUp() {
        roomAmenityRepository.deleteAll();
        roomRepository.deleteAll();
        amenityRepository.deleteAll();
        roomTypeRepository.deleteAll();
        hotelRepository.deleteAll();

        // Create hotel
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setAddress("123 Test Street");
        hotel = hotelRepository.save(hotel);

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
        testRoom.setHotel(hotel);
        testRoom.setRoomType(roomType);
        testRoom = roomRepository.save(testRoom);

        // Create amenities
        testAmenity1 = new Amenity();
        testAmenity1.setName("Chair");
        testAmenity1.setDescription("Comfortable office chair");
        testAmenity1 = amenityRepository.save(testAmenity1);

        testAmenity2 = new Amenity();
        testAmenity2.setName("WiFi");
        testAmenity2.setDescription("High-speed internet");
        testAmenity2 = amenityRepository.save(testAmenity2);
    }

    @Test
    @DisplayName("Should add amenity with quantity to room")
    void addAmenityWithQuantityToRoom() {
        RoomAmenityRequestDTO request = new RoomAmenityRequestDTO();
        request.setAmenityId(testAmenity1.getId());
        request.setQuantity(2);
        request.setPricePerUnit(new BigDecimal("5.00"));
        request.setNotes("Two comfortable chairs");

        RoomAmenityResponseDTO response = roomAmenityService.addAmenityToRoom(testRoom.getId(), request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(testRoom.getId(), response.getRoomId());
        assertEquals("101", response.getRoomNumber());
        assertEquals(testAmenity1.getId(), response.getAmenityId());
        assertEquals("Chair", response.getAmenityName());
        assertEquals(2, response.getQuantity());
        assertEquals(new BigDecimal("5.00"), response.getPricePerUnit());
        assertEquals(new BigDecimal("10.00"), response.getTotalPrice());
        assertEquals("Two comfortable chairs", response.getNotes());
    }

    @Test
    @DisplayName("Should calculate total price correctly (quantity * pricePerUnit)")
    void calculateTotalPriceCorrectly() {
        RoomAmenityRequestDTO request = new RoomAmenityRequestDTO();
        request.setAmenityId(testAmenity1.getId());
        request.setQuantity(4);
        request.setPricePerUnit(new BigDecimal("7.50"));

        RoomAmenityResponseDTO response = roomAmenityService.addAmenityToRoom(testRoom.getId(), request);

        // 4 * 7.50 = 30.00
        assertEquals(new BigDecimal("30.00"), response.getTotalPrice());
    }

    @Test
    @DisplayName("Should add amenity with zero price (free amenity)")
    void addFreeAmenity() {
        RoomAmenityRequestDTO request = new RoomAmenityRequestDTO();
        request.setAmenityId(testAmenity2.getId());
        request.setQuantity(1);
        request.setPricePerUnit(BigDecimal.ZERO);

        RoomAmenityResponseDTO response = roomAmenityService.addAmenityToRoom(testRoom.getId(), request);

        assertEquals(0, BigDecimal.ZERO.compareTo(response.getPricePerUnit()));
        assertEquals(0, BigDecimal.ZERO.compareTo(response.getTotalPrice()));
    }

    @Test
    @DisplayName("Should update amenity quantity")
    void updateAmenityQuantity() {
        // Add amenity first
        RoomAmenityRequestDTO addRequest = new RoomAmenityRequestDTO();
        addRequest.setAmenityId(testAmenity1.getId());
        addRequest.setQuantity(2);
        addRequest.setPricePerUnit(new BigDecimal("5.00"));
        roomAmenityService.addAmenityToRoom(testRoom.getId(), addRequest);

        // Update quantity
        RoomAmenityRequestDTO updateRequest = new RoomAmenityRequestDTO();
        updateRequest.setAmenityId(testAmenity1.getId());
        updateRequest.setQuantity(4);
        updateRequest.setPricePerUnit(new BigDecimal("5.00"));

        RoomAmenityResponseDTO updated = roomAmenityService.updateRoomAmenity(
                testRoom.getId(), testAmenity1.getId(), updateRequest);

        assertEquals(4, updated.getQuantity());
        assertEquals(new BigDecimal("20.00"), updated.getTotalPrice());
    }

    @Test
    @DisplayName("Should update amenity price")
    void updateAmenityPrice() {
        // Add amenity first
        RoomAmenityRequestDTO addRequest = new RoomAmenityRequestDTO();
        addRequest.setAmenityId(testAmenity1.getId());
        addRequest.setQuantity(2);
        addRequest.setPricePerUnit(new BigDecimal("5.00"));
        roomAmenityService.addAmenityToRoom(testRoom.getId(), addRequest);

        // Update price
        RoomAmenityRequestDTO updateRequest = new RoomAmenityRequestDTO();
        updateRequest.setAmenityId(testAmenity1.getId());
        updateRequest.setQuantity(2);
        updateRequest.setPricePerUnit(new BigDecimal("10.00"));

        RoomAmenityResponseDTO updated = roomAmenityService.updateRoomAmenity(
                testRoom.getId(), testAmenity1.getId(), updateRequest);

        assertEquals(new BigDecimal("10.00"), updated.getPricePerUnit());
        assertEquals(new BigDecimal("20.00"), updated.getTotalPrice());
    }

    @Test
    @DisplayName("Should get all amenities for room")
    void getAmenitiesByRoom() {
        // Add two amenities
        RoomAmenityRequestDTO request1 = new RoomAmenityRequestDTO();
        request1.setAmenityId(testAmenity1.getId());
        request1.setQuantity(2);
        request1.setPricePerUnit(new BigDecimal("5.00"));
        roomAmenityService.addAmenityToRoom(testRoom.getId(), request1);

        RoomAmenityRequestDTO request2 = new RoomAmenityRequestDTO();
        request2.setAmenityId(testAmenity2.getId());
        request2.setQuantity(1);
        request2.setPricePerUnit(BigDecimal.ZERO);
        roomAmenityService.addAmenityToRoom(testRoom.getId(), request2);

        List<RoomAmenityResponseDTO> amenities = roomAmenityService.getAmenitiesByRoom(testRoom.getId());

        assertEquals(2, amenities.size());
    }

    @Test
    @DisplayName("Should calculate total amenity price for room")
    void calculateTotalAmenityPriceForRoom() {
        // Add two amenities: 2 chairs @ $5 = $10, 1 WiFi @ $0 = $0
        RoomAmenityRequestDTO request1 = new RoomAmenityRequestDTO();
        request1.setAmenityId(testAmenity1.getId());
        request1.setQuantity(2);
        request1.setPricePerUnit(new BigDecimal("5.00"));
        roomAmenityService.addAmenityToRoom(testRoom.getId(), request1);

        RoomAmenityRequestDTO request2 = new RoomAmenityRequestDTO();
        request2.setAmenityId(testAmenity2.getId());
        request2.setQuantity(1);
        request2.setPricePerUnit(BigDecimal.ZERO);
        roomAmenityService.addAmenityToRoom(testRoom.getId(), request2);

        BigDecimal totalPrice = roomAmenityService.calculateTotalAmenityPrice(testRoom.getId());

        assertEquals(new BigDecimal("10.00"), totalPrice);
    }

    @Test
    @DisplayName("Should remove amenity from room")
    void removeAmenityFromRoom() {
        // Add amenity
        RoomAmenityRequestDTO request = new RoomAmenityRequestDTO();
        request.setAmenityId(testAmenity1.getId());
        request.setQuantity(2);
        request.setPricePerUnit(new BigDecimal("5.00"));
        roomAmenityService.addAmenityToRoom(testRoom.getId(), request);

        // Remove amenity
        roomAmenityService.removeAmenityFromRoom(testRoom.getId(), testAmenity1.getId());

        List<RoomAmenityResponseDTO> amenities = roomAmenityService.getAmenitiesByRoom(testRoom.getId());
        assertTrue(amenities.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when adding duplicate amenity")
    void throwExceptionForDuplicateAmenity() {
        RoomAmenityRequestDTO request = new RoomAmenityRequestDTO();
        request.setAmenityId(testAmenity1.getId());
        request.setQuantity(2);
        request.setPricePerUnit(new BigDecimal("5.00"));
        roomAmenityService.addAmenityToRoom(testRoom.getId(), request);

        assertThrows(RoomBadRequestException.class, () -> {
            roomAmenityService.addAmenityToRoom(testRoom.getId(), request);
        });
    }

    @Test
    @DisplayName("Should throw exception for non-existent room")
    void throwExceptionForNonExistentRoom() {
        RoomAmenityRequestDTO request = new RoomAmenityRequestDTO();
        request.setAmenityId(testAmenity1.getId());
        request.setQuantity(2);
        request.setPricePerUnit(new BigDecimal("5.00"));

        assertThrows(RoomNotFoundException.class, () -> {
            roomAmenityService.addAmenityToRoom(999L, request);
        });
    }

    @Test
    @DisplayName("Should throw exception for non-existent amenity")
    void throwExceptionForNonExistentAmenity() {
        RoomAmenityRequestDTO request = new RoomAmenityRequestDTO();
        request.setAmenityId(999L);
        request.setQuantity(2);
        request.setPricePerUnit(new BigDecimal("5.00"));

        assertThrows(AmenityNotFoundException.class, () -> {
            roomAmenityService.addAmenityToRoom(testRoom.getId(), request);
        });
    }

    @Test
    @DisplayName("Should get specific room amenity")
    void getSpecificRoomAmenity() {
        RoomAmenityRequestDTO request = new RoomAmenityRequestDTO();
        request.setAmenityId(testAmenity1.getId());
        request.setQuantity(3);
        request.setPricePerUnit(new BigDecimal("8.00"));
        roomAmenityService.addAmenityToRoom(testRoom.getId(), request);

        RoomAmenityResponseDTO response = roomAmenityService.getByRoomAndAmenity(
                testRoom.getId(), testAmenity1.getId());

        assertNotNull(response);
        assertEquals(3, response.getQuantity());
        assertEquals(new BigDecimal("8.00"), response.getPricePerUnit());
        assertEquals(new BigDecimal("24.00"), response.getTotalPrice());
    }
}
