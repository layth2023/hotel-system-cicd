package com.Amenity;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmenityServiceTest {

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private AmenityMapper amenityMapper;

    @InjectMocks
    private AmenityServiceImpl amenityService;

    private Amenity amenity;
    private AmenityRequestDTO requestDTO;
    private AmenityResponseDTO responseDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        amenity = new Amenity();
        amenity.setId(1L);
        amenity.setName("WiFi");
        amenity.setDescription("High-speed wireless internet");
        amenity.setActive(true);
        amenity.setHotels(new HashSet<>());

        requestDTO = new AmenityRequestDTO();
        requestDTO.setName("WiFi");
        requestDTO.setDescription("High-speed wireless internet");

        responseDTO = new AmenityResponseDTO(1L, "WiFi", "High-speed wireless internet", true);

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Create Amenity Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create amenity successfully")
        void shouldCreateAmenity() {
            when(amenityRepository.existsByNameInsensitive("WiFi")).thenReturn(false);
            when(amenityMapper.toEntity(any(AmenityRequestDTO.class))).thenReturn(amenity);
            when(amenityRepository.save(any(Amenity.class))).thenReturn(amenity);
            when(amenityMapper.toResponseDTO(any(Amenity.class))).thenReturn(responseDTO);

            AmenityResponseDTO result = amenityService.create(requestDTO);

            assertNotNull(result);
            assertEquals("WiFi", result.getName());
            verify(amenityRepository).save(any(Amenity.class));
        }

        @Test
        @DisplayName("Should throw exception when name exists")
        void shouldThrowExceptionWhenNameExists() {
            when(amenityRepository.existsByNameInsensitive("WiFi")).thenReturn(true);

            assertThrows(AmenityAlreadyExistsException.class, () -> amenityService.create(requestDTO));
        }

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameNull() {
            requestDTO.setName(null);

            assertThrows(IllegalArgumentException.class, () -> amenityService.create(requestDTO));
        }

        @Test
        @DisplayName("Should trim amenity name")
        void shouldTrimName() {
            requestDTO.setName("  WiFi  ");
            when(amenityRepository.existsByNameInsensitive("WiFi")).thenReturn(false);
            when(amenityMapper.toEntity(any(AmenityRequestDTO.class))).thenReturn(amenity);
            when(amenityRepository.save(any(Amenity.class))).thenReturn(amenity);
            when(amenityMapper.toResponseDTO(any(Amenity.class))).thenReturn(responseDTO);

            amenityService.create(requestDTO);

            assertEquals("WiFi", requestDTO.getName());
        }
    }

    @Nested
    @DisplayName("Get Amenity Tests")
    class GetTests {

        @Test
        @DisplayName("Should get amenity by id")
        void shouldGetById() {
            when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
            when(amenityMapper.toResponseDTO(amenity)).thenReturn(responseDTO);

            AmenityResponseDTO result = amenityService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw exception when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(amenityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(AmenityNotFoundException.class, () -> amenityService.getById(999L));
        }

        @Test
        @DisplayName("Should get all active amenities")
        void shouldGetAllActive() {
            Page<Amenity> amenityPage = new PageImpl<>(List.of(amenity));
            when(amenityRepository.findAllByActiveTrue(pageable)).thenReturn(amenityPage);
            when(amenityMapper.toResponseDTO(amenity)).thenReturn(responseDTO);

            Page<AmenityResponseDTO> result = amenityService.getAllActive(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should get all amenities")
        void shouldGetAll() {
            Page<Amenity> amenityPage = new PageImpl<>(List.of(amenity));
            when(amenityRepository.findAll(pageable)).thenReturn(amenityPage);
            when(amenityMapper.toResponseDTO(amenity)).thenReturn(responseDTO);

            Page<AmenityResponseDTO> result = amenityService.getAll(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should get all inactive amenities")
        void shouldGetAllInactive() {
            amenity.setActive(false);
            Page<Amenity> amenityPage = new PageImpl<>(List.of(amenity));
            when(amenityRepository.findAllByActiveFalse(pageable)).thenReturn(amenityPage);
            when(amenityMapper.toResponseDTO(amenity)).thenReturn(responseDTO);

            Page<AmenityResponseDTO> result = amenityService.getAllInactive(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Update Amenity Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update amenity successfully")
        void shouldUpdateAmenity() {
            when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
            when(amenityRepository.existsByNameInsensitiveAndIdNot("Updated WiFi", 1L)).thenReturn(false);
            when(amenityRepository.save(any(Amenity.class))).thenReturn(amenity);
            when(amenityMapper.toResponseDTO(any(Amenity.class))).thenReturn(responseDTO);

            AmenityRequestDTO updateDTO = new AmenityRequestDTO();
            updateDTO.setName("Updated WiFi");
            updateDTO.setDescription("Updated description");

            AmenityResponseDTO result = amenityService.update(1L, updateDTO);

            assertNotNull(result);
            verify(amenityMapper).updateEntity(any(Amenity.class), any(AmenityRequestDTO.class));
        }

        @Test
        @DisplayName("Should throw exception when updating to existing name")
        void shouldThrowExceptionWhenUpdatingToExistingName() {
            when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
            when(amenityRepository.existsByNameInsensitiveAndIdNot("Existing", 1L)).thenReturn(true);

            AmenityRequestDTO updateDTO = new AmenityRequestDTO();
            updateDTO.setName("Existing");

            assertThrows(AmenityAlreadyExistsException.class, () -> amenityService.update(1L, updateDTO));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent amenity")
        void shouldThrowExceptionWhenUpdatingNonExistent() {
            when(amenityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(AmenityNotFoundException.class, () -> amenityService.update(999L, requestDTO));
        }
    }

    @Nested
    @DisplayName("Delete Amenity Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should soft delete amenity")
        void shouldSoftDelete() {
            when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));

            amenityService.softDelete(1L);

            assertFalse(amenity.isActive());
        }

        @Test
        @DisplayName("Should hard delete amenity")
        void shouldHardDelete() {
            when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));

            amenityService.hardDelete(1L);

            verify(amenityRepository).delete(amenity);
        }

        @Test
        @DisplayName("Should throw exception when soft deleting non-existent")
        void shouldThrowExceptionWhenSoftDeletingNonExistent() {
            when(amenityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(AmenityNotFoundException.class, () -> amenityService.softDelete(999L));
        }

        @Test
        @DisplayName("Should throw exception when hard deleting non-existent")
        void shouldThrowExceptionWhenHardDeletingNonExistent() {
            when(amenityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(AmenityNotFoundException.class, () -> amenityService.hardDelete(999L));
        }
    }

    @Nested
    @DisplayName("Reactivate Amenity Tests")
    class ReactivateTests {

        @Test
        @DisplayName("Should reactivate amenity")
        void shouldReactivateAmenity() {
            amenity.setActive(false);
            when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));

            amenityService.reactivate(1L);

            assertTrue(amenity.isActive());
        }

        @Test
        @DisplayName("Should throw exception when reactivating non-existent")
        void shouldThrowExceptionWhenReactivatingNonExistent() {
            when(amenityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(AmenityNotFoundException.class, () -> amenityService.reactivate(999L));
        }
    }
}
