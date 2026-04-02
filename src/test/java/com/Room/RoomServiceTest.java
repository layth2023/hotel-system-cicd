package com.Room;

import com.Hotel.Hotel;
import com.Hotel.HotelNotFoundException;
import com.Hotel.HotelRepository;
import com.RoomType.RoomType;
import com.RoomType.RoomTypeNotFoundException;
import com.RoomType.RoomTypeRepository;
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

import com.Amenity.Amenity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Hotel hotel;
    private RoomType roomType;
    private Room room;
    private RoomRequestDTO requestDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
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

        requestDTO = new RoomRequestDTO();
        requestDTO.setRoomNumber("101");
        requestDTO.setFloor(1);
        requestDTO.setHotelId(1L);
        requestDTO.setRoomTypeId(1L);

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Create Room Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create room successfully")
        void shouldCreateRoom() {
            when(roomRepository.existsByRoomNumber("101")).thenReturn(false);
            when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
            when(roomRepository.save(any(Room.class))).thenReturn(room);

            RoomResponseDTO result = roomService.create(requestDTO);

            assertNotNull(result);
            assertEquals("101", result.getRoomNumber());
            verify(roomRepository).save(any(Room.class));
        }

        @Test
        @DisplayName("Should throw exception when room number exists")
        void shouldThrowExceptionWhenRoomNumberExists() {
            when(roomRepository.existsByRoomNumber("101")).thenReturn(true);

            assertThrows(RoomAlreadyExistsException.class, () -> roomService.create(requestDTO));
        }

        @Test
        @DisplayName("Should throw exception when room type not found")
        void shouldThrowExceptionWhenRoomTypeNotFound() {
            when(roomRepository.existsByRoomNumber("101")).thenReturn(false);
            when(roomTypeRepository.findById(999L)).thenReturn(Optional.empty());
            requestDTO.setRoomTypeId(999L);

            assertThrows(RoomTypeNotFoundException.class, () -> roomService.create(requestDTO));
        }

        @Test
        @DisplayName("Should throw exception when hotel not found")
        void shouldThrowExceptionWhenHotelNotFound() {
            when(roomRepository.existsByRoomNumber("101")).thenReturn(false);
            when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
            when(hotelRepository.findById(999L)).thenReturn(Optional.empty());
            requestDTO.setHotelId(999L);

            assertThrows(HotelNotFoundException.class, () -> roomService.create(requestDTO));
        }
    }

    @Nested
    @DisplayName("Get Room Tests")
    class GetTests {

        @Test
        @DisplayName("Should find room by id")
        void shouldFindById() {
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            RoomResponseDTO result = roomService.findById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw exception when room not found")
        void shouldThrowExceptionWhenNotFound() {
            when(roomRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RoomNotFoundException.class, () -> roomService.findById(999L));
        }

        @Test
        @DisplayName("Should find all rooms")
        void shouldFindAll() {
            when(roomRepository.findAll()).thenReturn(List.of(room));

            List<RoomResponseDTO> result = roomService.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should find rooms by hotel id")
        void shouldFindByHotelId() {
            when(hotelRepository.existsById(1L)).thenReturn(true);
            when(roomRepository.findByHotelId(1L)).thenReturn(List.of(room));

            List<RoomResponseDTO> result = roomService.findByHotelId(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should throw exception when hotel not found for rooms")
        void shouldThrowExceptionWhenHotelNotFoundForRooms() {
            when(hotelRepository.existsById(999L)).thenReturn(false);

            assertThrows(HotelNotFoundException.class, () -> roomService.findByHotelId(999L));
        }

        @Test
        @DisplayName("Should find rooms by hotel id paginated")
        void shouldFindByHotelIdPaginated() {
            Page<Room> roomPage = new PageImpl<>(List.of(room));
            when(hotelRepository.existsById(1L)).thenReturn(true);
            when(roomRepository.findByHotelId(1L, pageable)).thenReturn(roomPage);

            Page<RoomResponseDTO> result = roomService.findByHotelId(1L, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Update Room Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update room successfully")
        void shouldUpdateRoom() {
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
            when(roomRepository.save(any(Room.class))).thenReturn(room);

            RoomResponseDTO result = roomService.update(1L, requestDTO);

            assertNotNull(result);
            verify(roomRepository).save(any(Room.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent room")
        void shouldThrowExceptionWhenUpdatingNonExistent() {
            when(roomRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RoomNotFoundException.class, () -> roomService.update(999L, requestDTO));
        }

        @Test
        @DisplayName("Should throw exception when updating with non-existent room type")
        void shouldThrowExceptionWhenUpdatingWithNonExistentRoomType() {
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(roomTypeRepository.findById(999L)).thenReturn(Optional.empty());
            requestDTO.setRoomTypeId(999L);

            assertThrows(RoomTypeNotFoundException.class, () -> roomService.update(1L, requestDTO));
        }
    }

    @Nested
    @DisplayName("Delete Room Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete room")
        void shouldDeleteRoom() {
            when(roomRepository.existsById(1L)).thenReturn(true);

            roomService.deleteById(1L);

            verify(roomRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent room")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            when(roomRepository.existsById(999L)).thenReturn(false);

            assertThrows(RoomNotFoundException.class, () -> roomService.deleteById(999L));
        }
    }

    @Nested
    @DisplayName("Assign Room Type Tests")
    class AssignRoomTypeTests {

        @Test
        @DisplayName("Should assign room type")
        void shouldAssignRoomType() {
            RoomType newRoomType = new RoomType();
            newRoomType.setId(2L);
            newRoomType.setName("Suite");

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(roomTypeRepository.findById(2L)).thenReturn(Optional.of(newRoomType));
            when(roomRepository.save(any(Room.class))).thenReturn(room);

            RoomResponseDTO result = roomService.assignRoomType(1L, 2L);

            assertNotNull(result);
            assertEquals(newRoomType, room.getRoomType());
        }

        @Test
        @DisplayName("Should throw exception when room not found")
        void shouldThrowExceptionWhenRoomNotFound() {
            when(roomRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RoomNotFoundException.class, () -> roomService.assignRoomType(999L, 1L));
        }

        @Test
        @DisplayName("Should throw exception when room type not found")
        void shouldThrowExceptionWhenRoomTypeNotFound() {
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(roomTypeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RoomTypeNotFoundException.class, () -> roomService.assignRoomType(1L, 999L));
        }
    }

    @Nested
    @DisplayName("Availability Tests")
    class AvailabilityTests {

        @Test
        @DisplayName("Should find available rooms")
        void shouldFindAvailableRooms() {
            LocalDate checkIn = LocalDate.now().plusDays(1);
            LocalDate checkOut = LocalDate.now().plusDays(3);
            when(roomRepository.findAvailableRooms(checkIn, checkOut, 2)).thenReturn(List.of(room));

            List<RoomResponseDTO> result = roomService.findAvailableRooms(checkIn, checkOut, 2);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should throw exception when check-out before check-in")
        void shouldThrowExceptionWhenCheckOutBeforeCheckIn() {
            LocalDate checkIn = LocalDate.now().plusDays(3);
            LocalDate checkOut = LocalDate.now().plusDays(1);

            assertThrows(RoomBadRequestException.class, () ->
                    roomService.findAvailableRooms(checkIn, checkOut, 2));
        }

        @Test
        @DisplayName("Should throw exception when dates are equal")
        void shouldThrowExceptionWhenDatesEqual() {
            LocalDate date = LocalDate.now().plusDays(1);

            assertThrows(RoomBadRequestException.class, () ->
                    roomService.findAvailableRooms(date, date, 2));
        }

        @Test
        @DisplayName("Should find available rooms by hotel")
        void shouldFindAvailableRoomsByHotel() {
            LocalDate checkIn = LocalDate.now().plusDays(1);
            LocalDate checkOut = LocalDate.now().plusDays(3);
            when(hotelRepository.existsById(1L)).thenReturn(true);
            when(roomRepository.findAvailableRoomsByHotelAndDates(1L, checkIn, checkOut, 2))
                    .thenReturn(List.of(room));

            List<RoomResponseDTO> result = roomService.findAvailableRoomsByHotel(1L, checkIn, checkOut, 2);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should throw exception when hotel not found for availability")
        void shouldThrowExceptionWhenHotelNotFoundForAvailability() {
            LocalDate checkIn = LocalDate.now().plusDays(1);
            LocalDate checkOut = LocalDate.now().plusDays(3);
            when(hotelRepository.existsById(999L)).thenReturn(false);

            assertThrows(HotelNotFoundException.class, () ->
                    roomService.findAvailableRoomsByHotel(999L, checkIn, checkOut, 2));
        }
    }

    @Nested
    @DisplayName("Search Rooms Tests")
    class SearchTests {

        @Test
        @DisplayName("Should search rooms with filters")
        void shouldSearchRoomsWithFilters() {
            RoomSearchRequestDTO searchRequest = new RoomSearchRequestDTO();
            searchRequest.setHotelId(1L);
            searchRequest.setCheckInDate(LocalDate.now().plusDays(1));
            searchRequest.setCheckOutDate(LocalDate.now().plusDays(3));
            searchRequest.setGuests(2);
            searchRequest.setMinPrice(new BigDecimal("100.00"));
            searchRequest.setMaxPrice(new BigDecimal("200.00"));

            when(roomRepository.findAvailableRoomsByHotelAndDates(anyLong(), any(LocalDate.class), any(LocalDate.class), anyInt()))
                    .thenReturn(List.of(room));

            List<RoomResponseDTO> result = roomService.searchRooms(searchRequest);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should filter rooms by price range")
        void shouldFilterRoomsByPriceRange() {
            RoomSearchRequestDTO searchRequest = new RoomSearchRequestDTO();
            searchRequest.setCheckInDate(LocalDate.now().plusDays(1));
            searchRequest.setCheckOutDate(LocalDate.now().plusDays(3));
            searchRequest.setMinPrice(new BigDecimal("100.00"));
            searchRequest.setMaxPrice(new BigDecimal("200.00"));

            when(roomRepository.findAvailableRooms(any(LocalDate.class), any(LocalDate.class), anyInt()))
                    .thenReturn(List.of(room));

            List<RoomResponseDTO> result = roomService.searchRooms(searchRequest);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should filter rooms by amenity")
        void shouldFilterRoomsByAmenity() {
            Amenity wifi = new Amenity("WiFi");
            wifi.setId(1L);
            room.setAmenities(Set.of(wifi));

            RoomSearchRequestDTO searchRequest = new RoomSearchRequestDTO();
            searchRequest.setCheckInDate(LocalDate.now().plusDays(1));
            searchRequest.setCheckOutDate(LocalDate.now().plusDays(3));
            searchRequest.setAmenity("wifi");

            when(roomRepository.findAvailableRooms(any(LocalDate.class), any(LocalDate.class), anyInt()))
                    .thenReturn(List.of(room));

            List<RoomResponseDTO> result = roomService.searchRooms(searchRequest);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should filter rooms by amenity case insensitive")
        void shouldFilterRoomsByAmenityCaseInsensitive() {
            Amenity pool = new Amenity("Swimming Pool");
            pool.setId(1L);
            room.setAmenities(Set.of(pool));

            RoomSearchRequestDTO searchRequest = new RoomSearchRequestDTO();
            searchRequest.setCheckInDate(LocalDate.now().plusDays(1));
            searchRequest.setCheckOutDate(LocalDate.now().plusDays(3));
            searchRequest.setAmenity("SWIMMING");

            when(roomRepository.findAvailableRooms(any(LocalDate.class), any(LocalDate.class), anyInt()))
                    .thenReturn(List.of(room));

            List<RoomResponseDTO> result = roomService.searchRooms(searchRequest);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should exclude rooms without matching amenity")
        void shouldExcludeRoomsWithoutMatchingAmenity() {
            Amenity tv = new Amenity("TV");
            tv.setId(1L);
            room.setAmenities(Set.of(tv));

            RoomSearchRequestDTO searchRequest = new RoomSearchRequestDTO();
            searchRequest.setCheckInDate(LocalDate.now().plusDays(1));
            searchRequest.setCheckOutDate(LocalDate.now().plusDays(3));
            searchRequest.setAmenity("pool");

            when(roomRepository.findAvailableRooms(any(LocalDate.class), any(LocalDate.class), anyInt()))
                    .thenReturn(List.of(room));

            List<RoomResponseDTO> result = roomService.searchRooms(searchRequest);

            assertNotNull(result);
            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("Should return rooms when amenity filter is blank")
        void shouldReturnRoomsWhenAmenityFilterIsBlank() {
            RoomSearchRequestDTO searchRequest = new RoomSearchRequestDTO();
            searchRequest.setCheckInDate(LocalDate.now().plusDays(1));
            searchRequest.setCheckOutDate(LocalDate.now().plusDays(3));
            searchRequest.setAmenity("   ");

            when(roomRepository.findAvailableRooms(any(LocalDate.class), any(LocalDate.class), anyInt()))
                    .thenReturn(List.of(room));

            List<RoomResponseDTO> result = roomService.searchRooms(searchRequest);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should filter rooms by room type name")
        void shouldFilterRoomsByRoomTypeName() {
            RoomSearchRequestDTO searchRequest = new RoomSearchRequestDTO();
            searchRequest.setCheckInDate(LocalDate.now().plusDays(1));
            searchRequest.setCheckOutDate(LocalDate.now().plusDays(3));
            searchRequest.setRoomTypeName("Deluxe");

            when(roomRepository.findAvailableRooms(any(LocalDate.class), any(LocalDate.class), anyInt()))
                    .thenReturn(List.of(room));

            List<RoomResponseDTO> result = roomService.searchRooms(searchRequest);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should filter rooms by multiple criteria")
        void shouldFilterRoomsByMultipleCriteria() {
            Amenity wifi = new Amenity("WiFi");
            wifi.setId(1L);
            room.setAmenities(Set.of(wifi));

            RoomSearchRequestDTO searchRequest = new RoomSearchRequestDTO();
            searchRequest.setCheckInDate(LocalDate.now().plusDays(1));
            searchRequest.setCheckOutDate(LocalDate.now().plusDays(3));
            searchRequest.setMinPrice(new BigDecimal("100.00"));
            searchRequest.setMaxPrice(new BigDecimal("200.00"));
            searchRequest.setRoomTypeName("Deluxe");
            searchRequest.setAmenity("wifi");

            when(roomRepository.findAvailableRooms(any(LocalDate.class), any(LocalDate.class), anyInt()))
                    .thenReturn(List.of(room));

            List<RoomResponseDTO> result = roomService.searchRooms(searchRequest);

            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }
}
