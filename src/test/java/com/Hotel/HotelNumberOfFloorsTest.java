package com.Hotel;

import com.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Hotel numberOfFloors field.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class HotelNumberOfFloorsTest {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private HotelRepository hotelRepository;

    private HotelRequestDTO hotelRequest;

    @BeforeEach
    void setUp() {
        hotelRepository.deleteAll();

        hotelRequest = new HotelRequestDTO();
        hotelRequest.setName("Test Hotel");
        hotelRequest.setAddress("123 Test Street");
        hotelRequest.setCity("Test City");
        hotelRequest.setCountry("Test Country");
        hotelRequest.setPhone("+1234567890");
        hotelRequest.setEmail("test@hotel.com");
    }

    @Test
    @DisplayName("Should create hotel with numberOfFloors")
    void createHotelWithNumberOfFloors() {
        hotelRequest.setNumberOfFloors(15);

        HotelResponseDTO response = hotelService.create(hotelRequest);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(15, response.getNumberOfFloors());
    }

    @Test
    @DisplayName("Should create hotel without numberOfFloors (null)")
    void createHotelWithoutNumberOfFloors() {
        hotelRequest.setNumberOfFloors(null);

        HotelResponseDTO response = hotelService.create(hotelRequest);

        assertNotNull(response);
        assertNull(response.getNumberOfFloors());
    }

    @Test
    @DisplayName("Should update hotel numberOfFloors")
    void updateHotelNumberOfFloors() {
        hotelRequest.setNumberOfFloors(10);
        HotelResponseDTO created = hotelService.create(hotelRequest);

        HotelRequestDTO updateRequest = new HotelRequestDTO();
        updateRequest.setName("Test Hotel");
        updateRequest.setAddress("123 Test Street");
        updateRequest.setNumberOfFloors(20);

        HotelResponseDTO updated = hotelService.update(created.getId(), updateRequest);

        assertEquals(20, updated.getNumberOfFloors());
    }

    @Test
    @DisplayName("Should retrieve hotel with numberOfFloors")
    void getHotelWithNumberOfFloors() {
        hotelRequest.setNumberOfFloors(25);
        HotelResponseDTO created = hotelService.create(hotelRequest);

        HotelResponseDTO retrieved = hotelService.getById(created.getId());

        assertNotNull(retrieved);
        assertEquals(25, retrieved.getNumberOfFloors());
    }

    @Test
    @DisplayName("Hotel entity should have numberOfFloors field")
    void hotelEntityHasNumberOfFloorsField() {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setAddress("123 Test Street");
        hotel.setNumberOfFloors(30);

        Hotel saved = hotelRepository.save(hotel);

        assertNotNull(saved.getId());
        assertEquals(30, saved.getNumberOfFloors());
    }

    @Test
    @DisplayName("Should allow zero floors")
    void allowZeroFloors() {
        hotelRequest.setNumberOfFloors(0);

        HotelResponseDTO response = hotelService.create(hotelRequest);

        assertEquals(0, response.getNumberOfFloors());
    }
}
