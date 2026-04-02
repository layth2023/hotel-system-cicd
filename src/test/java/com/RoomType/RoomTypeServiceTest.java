package com.RoomType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomTypeServiceTest {

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private RoomTypeMapper roomTypeMapper;

    @InjectMocks
    private RoomTypeServiceImpl roomTypeService;

    private RoomType roomType;
    private RoomTypeRequestDTO requestDTO;
    private RoomTypeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        roomType = new RoomType();
        roomType.setId(1L);
        roomType.setName("Deluxe Suite");
        roomType.setDescription("Luxurious suite with city view");
        roomType.setCapacity(4);
        roomType.setBeds(2);
        roomType.setPricePerNight(new BigDecimal("150.00"));
        roomType.setCancellationRules("Free cancellation up to 24 hours");

        requestDTO = new RoomTypeRequestDTO();
        requestDTO.setName("Deluxe Suite");
        requestDTO.setDescription("Luxurious suite with city view");
        requestDTO.setCapacity(4);
        requestDTO.setBeds(2);
        requestDTO.setPricePerNight(new BigDecimal("150.00"));
        requestDTO.setCancellationRules("Free cancellation up to 24 hours");

        responseDTO = new RoomTypeResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Deluxe Suite");
        responseDTO.setDescription("Luxurious suite with city view");
        responseDTO.setCapacity(4);
        responseDTO.setBeds(2);
        responseDTO.setPricePerNight(new BigDecimal("150.00"));
    }

    @Nested
    @DisplayName("Create RoomType Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create room type successfully")
        void shouldCreateRoomType() {
            when(roomTypeRepository.existsByName("Deluxe Suite")).thenReturn(false);
            when(roomTypeMapper.toEntity(any(RoomTypeRequestDTO.class))).thenReturn(roomType);
            when(roomTypeRepository.save(any(RoomType.class))).thenReturn(roomType);
            when(roomTypeMapper.toDto(any(RoomType.class))).thenReturn(responseDTO);

            RoomTypeResponseDTO result = roomTypeService.create(requestDTO);

            assertNotNull(result);
            assertEquals("Deluxe Suite", result.getName());
            verify(roomTypeRepository).save(any(RoomType.class));
        }

        @Test
        @DisplayName("Should throw exception when name exists")
        void shouldThrowExceptionWhenNameExists() {
            when(roomTypeRepository.existsByName("Deluxe Suite")).thenReturn(true);

            assertThrows(RoomTypeAlreadyExistsException.class, () -> roomTypeService.create(requestDTO));
            verify(roomTypeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get RoomType Tests")
    class GetTests {

        @Test
        @DisplayName("Should find room type by id")
        void shouldFindById() {
            when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
            when(roomTypeMapper.toDto(roomType)).thenReturn(responseDTO);

            RoomTypeResponseDTO result = roomTypeService.findById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Deluxe Suite", result.getName());
        }

        @Test
        @DisplayName("Should throw exception when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(roomTypeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RoomTypeNotFoundException.class, () -> roomTypeService.findById(999L));
        }

        @Test
        @DisplayName("Should find all room types")
        void shouldFindAll() {
            when(roomTypeRepository.findAll()).thenReturn(List.of(roomType));
            when(roomTypeMapper.toDto(roomType)).thenReturn(responseDTO);

            List<RoomTypeResponseDTO> result = roomTypeService.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Deluxe Suite", result.get(0).getName());
        }

        @Test
        @DisplayName("Should return empty list when no room types")
        void shouldReturnEmptyListWhenNoRoomTypes() {
            when(roomTypeRepository.findAll()).thenReturn(List.of());

            List<RoomTypeResponseDTO> result = roomTypeService.findAll();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Update RoomType Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update room type successfully")
        void shouldUpdateRoomType() {
            when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
            when(roomTypeRepository.save(any(RoomType.class))).thenReturn(roomType);
            when(roomTypeMapper.toDto(any(RoomType.class))).thenReturn(responseDTO);

            RoomTypeRequestDTO updateDTO = new RoomTypeRequestDTO();
            updateDTO.setName("Updated Suite");
            updateDTO.setDescription("Updated description");
            updateDTO.setCapacity(6);
            updateDTO.setBeds(3);
            updateDTO.setPricePerNight(new BigDecimal("200.00"));

            RoomTypeResponseDTO result = roomTypeService.update(1L, updateDTO);

            assertNotNull(result);
            verify(roomTypeMapper).updateEntity(any(RoomType.class), any(RoomTypeRequestDTO.class));
            verify(roomTypeRepository).save(any(RoomType.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent")
        void shouldThrowExceptionWhenUpdatingNonExistent() {
            when(roomTypeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RoomTypeNotFoundException.class, () -> roomTypeService.update(999L, requestDTO));
        }

        @Test
        @DisplayName("Should update price correctly")
        void shouldUpdatePriceCorrectly() {
            when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
            when(roomTypeRepository.save(any(RoomType.class))).thenReturn(roomType);
            when(roomTypeMapper.toDto(any(RoomType.class))).thenReturn(responseDTO);

            requestDTO.setPricePerNight(new BigDecimal("250.00"));
            roomTypeService.update(1L, requestDTO);

            verify(roomTypeMapper).updateEntity(eq(roomType), eq(requestDTO));
        }

        @Test
        @DisplayName("Should update capacity correctly")
        void shouldUpdateCapacityCorrectly() {
            when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
            when(roomTypeRepository.save(any(RoomType.class))).thenReturn(roomType);
            when(roomTypeMapper.toDto(any(RoomType.class))).thenReturn(responseDTO);

            requestDTO.setCapacity(8);
            roomTypeService.update(1L, requestDTO);

            verify(roomTypeMapper).updateEntity(eq(roomType), eq(requestDTO));
        }
    }

    @Nested
    @DisplayName("Delete RoomType Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete room type")
        void shouldDeleteRoomType() {
            when(roomTypeRepository.existsById(1L)).thenReturn(true);

            roomTypeService.deleteById(1L);

            verify(roomTypeRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            when(roomTypeRepository.existsById(999L)).thenReturn(false);

            assertThrows(RoomTypeNotFoundException.class, () -> roomTypeService.deleteById(999L));
        }
    }
}
