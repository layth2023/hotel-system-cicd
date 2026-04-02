package com.Room;

import com.Amenity.Amenity;
import com.Amenity.AmenityRepository;
import com.Hotel.Hotel;
import com.Hotel.HotelRepository;
import com.Role.Role;
import com.Role.RoleRepository;
import com.RoomType.RoomType;
import com.RoomType.RoomTypeRepository;
import com.Security.JwtTokenProvider;
import com.TestConfig;
import com.User.User;
import com.User.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for RoomAmenityController endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class RoomAmenityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

    private String adminToken;
    private Room testRoom;
    private Amenity testAmenity;

    @BeforeEach
    void setUp() {
        roomAmenityRepository.deleteAll();
        roomRepository.deleteAll();
        amenityRepository.deleteAll();
        roomTypeRepository.deleteAll();
        hotelRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create roles
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole = roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        roleRepository.save(userRole);

        // Create admin user
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword("password");
        adminUser.addRole(adminRole);
        userRepository.save(adminUser);

        // Generate JWT token for admin using username directly
        adminToken = jwtTokenProvider.generateTokenFromUsername("admin", "ROLE_ADMIN");

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

        // Create amenity
        testAmenity = new Amenity();
        testAmenity.setName("Chair");
        testAmenity.setDescription("Comfortable chair");
        testAmenity = amenityRepository.save(testAmenity);
    }

    @Test
    @DisplayName("POST /rooms/{roomId}/amenities - Should add amenity with quantity")
    void addAmenityToRoom() throws Exception {
        RoomAmenityRequestDTO request = new RoomAmenityRequestDTO();
        request.setAmenityId(testAmenity.getId());
        request.setQuantity(2);
        request.setPricePerUnit(new BigDecimal("5.00"));
        request.setNotes("Two chairs");

        mockMvc.perform(post("/rooms/{roomId}/amenities", testRoom.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.roomId").value(testRoom.getId()))
                .andExpect(jsonPath("$.roomNumber").value("101"))
                .andExpect(jsonPath("$.amenityId").value(testAmenity.getId()))
                .andExpect(jsonPath("$.amenityName").value("Chair"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.pricePerUnit").value(5.00))
                .andExpect(jsonPath("$.totalPrice").value(10.00))
                .andExpect(jsonPath("$.notes").value("Two chairs"));
    }

    @Test
    @DisplayName("GET /rooms/{roomId}/amenities - Should list room amenities")
    void getAmenitiesByRoom() throws Exception {
        // Add amenity first
        RoomAmenity roomAmenity = new RoomAmenity();
        roomAmenity.setRoom(testRoom);
        roomAmenity.setAmenity(testAmenity);
        roomAmenity.setQuantity(3);
        roomAmenity.setPricePerUnit(new BigDecimal("8.00"));
        roomAmenityRepository.save(roomAmenity);

        mockMvc.perform(get("/rooms/{roomId}/amenities", testRoom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amenityName").value("Chair"))
                .andExpect(jsonPath("$[0].quantity").value(3))
                .andExpect(jsonPath("$[0].totalPrice").value(24.00));
    }

    @Test
    @DisplayName("GET /rooms/{roomId}/amenities/{amenityId} - Should get specific room amenity")
    void getSpecificRoomAmenity() throws Exception {
        // Add amenity first
        RoomAmenity roomAmenity = new RoomAmenity();
        roomAmenity.setRoom(testRoom);
        roomAmenity.setAmenity(testAmenity);
        roomAmenity.setQuantity(2);
        roomAmenity.setPricePerUnit(new BigDecimal("5.00"));
        roomAmenityRepository.save(roomAmenity);

        mockMvc.perform(get("/rooms/{roomId}/amenities/{amenityId}", testRoom.getId(), testAmenity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amenityId").value(testAmenity.getId()))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(10.00));
    }

    @Test
    @DisplayName("PUT /rooms/{roomId}/amenities/{amenityId} - Should update room amenity")
    void updateRoomAmenity() throws Exception {
        // Add amenity first
        RoomAmenity roomAmenity = new RoomAmenity();
        roomAmenity.setRoom(testRoom);
        roomAmenity.setAmenity(testAmenity);
        roomAmenity.setQuantity(2);
        roomAmenity.setPricePerUnit(new BigDecimal("5.00"));
        roomAmenityRepository.save(roomAmenity);

        RoomAmenityRequestDTO updateRequest = new RoomAmenityRequestDTO();
        updateRequest.setAmenityId(testAmenity.getId());
        updateRequest.setQuantity(4);
        updateRequest.setPricePerUnit(new BigDecimal("6.00"));

        mockMvc.perform(put("/rooms/{roomId}/amenities/{amenityId}", testRoom.getId(), testAmenity.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(4))
                .andExpect(jsonPath("$.pricePerUnit").value(6.00))
                .andExpect(jsonPath("$.totalPrice").value(24.00));
    }

    @Test
    @DisplayName("DELETE /rooms/{roomId}/amenities/{amenityId} - Should remove amenity from room")
    void removeAmenityFromRoom() throws Exception {
        // Add amenity first
        RoomAmenity roomAmenity = new RoomAmenity();
        roomAmenity.setRoom(testRoom);
        roomAmenity.setAmenity(testAmenity);
        roomAmenity.setQuantity(2);
        roomAmenity.setPricePerUnit(new BigDecimal("5.00"));
        roomAmenityRepository.save(roomAmenity);

        mockMvc.perform(delete("/rooms/{roomId}/amenities/{amenityId}", testRoom.getId(), testAmenity.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/rooms/{roomId}/amenities", testRoom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /rooms/{roomId}/amenities/total-price - Should calculate total amenity price")
    void calculateTotalAmenityPrice() throws Exception {
        // Add two amenities
        RoomAmenity roomAmenity1 = new RoomAmenity();
        roomAmenity1.setRoom(testRoom);
        roomAmenity1.setAmenity(testAmenity);
        roomAmenity1.setQuantity(2);
        roomAmenity1.setPricePerUnit(new BigDecimal("5.00"));
        roomAmenityRepository.save(roomAmenity1);

        Amenity amenity2 = new Amenity();
        amenity2.setName("WiFi");
        amenity2 = amenityRepository.save(amenity2);

        RoomAmenity roomAmenity2 = new RoomAmenity();
        roomAmenity2.setRoom(testRoom);
        roomAmenity2.setAmenity(amenity2);
        roomAmenity2.setQuantity(1);
        roomAmenity2.setPricePerUnit(new BigDecimal("3.00"));
        roomAmenityRepository.save(roomAmenity2);

        mockMvc.perform(get("/rooms/{roomId}/amenities/total-price", testRoom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(testRoom.getId()))
                .andExpect(jsonPath("$.totalAmenityPrice").value(13.00)); // 10 + 3 = 13
    }

    @Test
    @DisplayName("Should require authentication for POST")
    void requireAuthForPost() throws Exception {
        RoomAmenityRequestDTO request = new RoomAmenityRequestDTO();
        request.setAmenityId(testAmenity.getId());
        request.setQuantity(2);

        mockMvc.perform(post("/rooms/{roomId}/amenities", testRoom.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 404 for non-existent room")
    void return404ForNonExistentRoom() throws Exception {
        RoomAmenityRequestDTO request = new RoomAmenityRequestDTO();
        request.setAmenityId(testAmenity.getId());
        request.setQuantity(2);

        mockMvc.perform(post("/rooms/{roomId}/amenities", 999L)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 for duplicate amenity")
    void return400ForDuplicateAmenity() throws Exception {
        // Add amenity first
        RoomAmenity roomAmenity = new RoomAmenity();
        roomAmenity.setRoom(testRoom);
        roomAmenity.setAmenity(testAmenity);
        roomAmenity.setQuantity(2);
        roomAmenity.setPricePerUnit(new BigDecimal("5.00"));
        roomAmenityRepository.save(roomAmenity);

        // Try to add same amenity again
        RoomAmenityRequestDTO request = new RoomAmenityRequestDTO();
        request.setAmenityId(testAmenity.getId());
        request.setQuantity(3);

        mockMvc.perform(post("/rooms/{roomId}/amenities", testRoom.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
