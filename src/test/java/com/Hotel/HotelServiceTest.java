package com.Hotel;

import com.Amenity.Amenity;
import com.Amenity.AmenityMapper;
import com.Amenity.AmenityNotFoundException;
import com.Amenity.AmenityRepository;
import com.Room.RoomRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelMapper hotelMapper;

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private AmenityMapper amenityMapper;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private Hotel hotel;
    private HotelRequestDTO requestDTO;
    private HotelResponseDTO responseDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        hotel.setAddress("123 Test Street");
        hotel.setCity("Test City");
        hotel.setCountry("Test Country");
        hotel.setAmenities(new HashSet<>());

        requestDTO = new HotelRequestDTO();
        requestDTO.setName("Test Hotel");
        requestDTO.setAddress("123 Test Street");
        requestDTO.setCity("Test City");
        requestDTO.setCountry("Test Country");

        responseDTO = new HotelResponseDTO(
                1L, "Test Hotel", "123 Test Street", "Test City",
                "Test Country", null, null, null, Set.of()
        );

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Create Hotel Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create hotel successfully")
        void shouldCreateHotel() {
            when(hotelRepository.existsByNameInsensitive(anyString())).thenReturn(false);
            when(hotelMapper.toEntity(any(HotelRequestDTO.class))).thenReturn(hotel);
            when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);
            when(hotelMapper.toResponseDTO(any(Hotel.class))).thenReturn(responseDTO);

            HotelResponseDTO result = hotelService.create(requestDTO);

            assertNotNull(result);
            assertEquals("Test Hotel", result.getName());
            verify(hotelRepository).save(any(Hotel.class));
        }

        @Test
        @DisplayName("Should throw exception when hotel name exists")
        void shouldThrowExceptionWhenNameExists() {
            when(hotelRepository.existsByNameInsensitive(anyString())).thenReturn(true);

            assertThrows(HotelAlreadyExistsException.class, () -> hotelService.create(requestDTO));
            verify(hotelRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameNull() {
            requestDTO.setName(null);

            assertThrows(IllegalArgumentException.class, () -> hotelService.create(requestDTO));
        }

        @Test
        @DisplayName("Should trim hotel name on create")
        void shouldTrimHotelName() {
            requestDTO.setName("  Test Hotel  ");
            when(hotelRepository.existsByNameInsensitive("Test Hotel")).thenReturn(false);
            when(hotelMapper.toEntity(any(HotelRequestDTO.class))).thenReturn(hotel);
            when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);
            when(hotelMapper.toResponseDTO(any(Hotel.class))).thenReturn(responseDTO);

            hotelService.create(requestDTO);

            assertEquals("Test Hotel", requestDTO.getName());
        }
    }

    @Nested
    @DisplayName("Get Hotel Tests")
    class GetTests {

        @Test
        @DisplayName("Should get hotel by id")
        void shouldGetById() {
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
            when(hotelMapper.toResponseDTO(hotel)).thenReturn(responseDTO);

            HotelResponseDTO result = hotelService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw exception when hotel not found")
        void shouldThrowExceptionWhenNotFound() {
            when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(HotelNotFoundException.class, () -> hotelService.getById(999L));
        }

        @Test
        @DisplayName("Should get all hotels paginated")
        void shouldGetAllPaginated() {
            Page<Hotel> hotelPage = new PageImpl<>(List.of(hotel));
            when(hotelRepository.findAll(pageable)).thenReturn(hotelPage);
            when(hotelMapper.toResponseDTO(hotel)).thenReturn(responseDTO);

            Page<HotelResponseDTO> result = hotelService.getAll(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Update Hotel Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update hotel successfully")
        void shouldUpdateHotel() {
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
            when(hotelRepository.existsByNameInsensitiveAndIdNot("Updated Hotel", 1L)).thenReturn(false);
            when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);
            when(hotelMapper.toResponseDTO(any(Hotel.class))).thenReturn(responseDTO);

            HotelRequestDTO updateDTO = new HotelRequestDTO();
            updateDTO.setName("Updated Hotel");
            updateDTO.setAddress("456 New Street");

            HotelResponseDTO result = hotelService.update(1L, updateDTO);

            assertNotNull(result);
            verify(hotelMapper).updateEntity(any(Hotel.class), any(HotelRequestDTO.class));
        }

        @Test
        @DisplayName("Should throw exception when updating to existing name")
        void shouldThrowExceptionWhenUpdatingToExistingName() {
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
            when(hotelRepository.existsByNameInsensitiveAndIdNot("Existing Hotel", 1L)).thenReturn(true);

            HotelRequestDTO updateDTO = new HotelRequestDTO();
            updateDTO.setName("Existing Hotel");
            updateDTO.setAddress("123 Test");

            assertThrows(HotelAlreadyExistsException.class, () -> hotelService.update(1L, updateDTO));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent hotel")
        void shouldThrowExceptionWhenUpdatingNonExistent() {
            when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(HotelNotFoundException.class, () -> hotelService.update(999L, requestDTO));
        }
    }

    @Nested
    @DisplayName("Delete Hotel Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete hotel successfully")
        void shouldDeleteHotel() {
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

            hotelService.delete(1L);

            verify(hotelRepository).delete(hotel);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent hotel")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(HotelNotFoundException.class, () -> hotelService.delete(999L));
        }
    }

    @Nested
    @DisplayName("Amenity Management Tests")
    class AmenityTests {

        private Amenity amenity;

        @BeforeEach
        void setUpAmenity() {
            amenity = new Amenity();
            amenity.setId(1L);
            amenity.setName("WiFi");
            amenity.setHotels(new HashSet<>());
        }

        @Test
        @DisplayName("Should add amenity to hotel")
        void shouldAddAmenity() {
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
            when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));

            hotelService.addAmenity(1L, 1L);

            assertTrue(hotel.getAmenities().contains(amenity));
            assertTrue(amenity.getHotels().contains(hotel));
        }

        @Test
        @DisplayName("Should throw exception when adding amenity to non-existent hotel")
        void shouldThrowExceptionWhenAddingAmenityToNonExistentHotel() {
            when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(HotelNotFoundException.class, () -> hotelService.addAmenity(999L, 1L));
        }

        @Test
        @DisplayName("Should throw exception when adding non-existent amenity")
        void shouldThrowExceptionWhenAddingNonExistentAmenity() {
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
            when(amenityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(AmenityNotFoundException.class, () -> hotelService.addAmenity(1L, 999L));
        }

        @Test
        @DisplayName("Should remove amenity from hotel")
        void shouldRemoveAmenity() {
            hotel.getAmenities().add(amenity);
            amenity.getHotels().add(hotel);
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
            when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));

            hotelService.removeAmenity(1L, 1L);

            assertFalse(hotel.getAmenities().contains(amenity));
            assertFalse(amenity.getHotels().contains(hotel));
        }
    }

    @Nested
    @DisplayName("Search Hotels Tests")
    class SearchTests {

        @Test
        @DisplayName("Should search hotels by city")
        void shouldSearchByCity() {
            Page<Hotel> hotelPage = new PageImpl<>(List.of(hotel));
            when(hotelRepository.searchHotels("Test City", null, null, pageable)).thenReturn(hotelPage);
            when(hotelMapper.toResponseDTO(hotel)).thenReturn(responseDTO);

            Page<HotelResponseDTO> result = hotelService.searchHotels("Test City", null, null, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should search hotels by country")
        void shouldSearchByCountry() {
            Page<Hotel> hotelPage = new PageImpl<>(List.of(hotel));
            when(hotelRepository.searchHotels(null, "Test Country", null, pageable)).thenReturn(hotelPage);
            when(hotelMapper.toResponseDTO(hotel)).thenReturn(responseDTO);

            Page<HotelResponseDTO> result = hotelService.searchHotels(null, "Test Country", null, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should search hotels by star rating")
        void shouldSearchByStarRating() {
            Page<Hotel> hotelPage = new PageImpl<>(List.of(hotel));
            when(hotelRepository.searchHotels(null, null, 4, pageable)).thenReturn(hotelPage);
            when(hotelMapper.toResponseDTO(hotel)).thenReturn(responseDTO);

            Page<HotelResponseDTO> result = hotelService.searchHotels(null, null, 4, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Hotel Rooms Tests")
    class HotelRoomsTests {

        @Test
        @DisplayName("Should get hotel rooms")
        void shouldGetHotelRooms() {
            when(hotelRepository.existsById(1L)).thenReturn(true);
            when(roomRepository.findByHotelId(1L)).thenReturn(List.of());

            var result = hotelService.getHotelRooms(1L);

            assertNotNull(result);
            verify(roomRepository).findByHotelId(1L);
        }

        @Test
        @DisplayName("Should throw exception when getting rooms for non-existent hotel")
        void shouldThrowExceptionWhenGettingRoomsForNonExistentHotel() {
            when(hotelRepository.existsById(999L)).thenReturn(false);

            assertThrows(HotelNotFoundException.class, () -> hotelService.getHotelRooms(999L));
        }
    }
}
