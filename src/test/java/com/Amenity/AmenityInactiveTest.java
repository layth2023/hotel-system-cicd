package com.Amenity;

import com.TestConfig;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Inactive Amenities functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class AmenityInactiveTest {

    @Autowired
    private AmenityService amenityService;

    @Autowired
    private AmenityRepository amenityRepository;

    @BeforeEach
    void setUp() {
        amenityRepository.deleteAll();
    }

    @Test
    @DisplayName("Should list all inactive amenities")
    void listAllInactiveAmenities() {
        // Create active amenities
        AmenityRequestDTO activeRequest1 = new AmenityRequestDTO();
        activeRequest1.setName("WiFi");
        activeRequest1.setDescription("High-speed internet");
        activeRequest1.setIsActive(true);
        amenityService.create(activeRequest1);

        AmenityRequestDTO activeRequest2 = new AmenityRequestDTO();
        activeRequest2.setName("Pool");
        activeRequest2.setDescription("Swimming pool");
        activeRequest2.setIsActive(true);
        amenityService.create(activeRequest2);

        // Create inactive amenities
        AmenityRequestDTO inactiveRequest1 = new AmenityRequestDTO();
        inactiveRequest1.setName("Spa");
        inactiveRequest1.setDescription("Relaxing spa");
        inactiveRequest1.setIsActive(false);
        amenityService.create(inactiveRequest1);

        AmenityRequestDTO inactiveRequest2 = new AmenityRequestDTO();
        inactiveRequest2.setName("Mini Bar");
        inactiveRequest2.setDescription("In-room mini bar");
        inactiveRequest2.setIsActive(false);
        amenityService.create(inactiveRequest2);

        Page<AmenityResponseDTO> inactiveAmenities = amenityService.getAllInactive(PageRequest.of(0, 10));

        assertEquals(2, inactiveAmenities.getTotalElements());
        assertTrue(inactiveAmenities.getContent().stream().allMatch(a -> !a.getIsActive()));
    }

    @Test
    @DisplayName("Should return empty page when no inactive amenities")
    void returnEmptyPageWhenNoInactiveAmenities() {
        // Create only active amenities
        AmenityRequestDTO activeRequest = new AmenityRequestDTO();
        activeRequest.setName("WiFi");
        activeRequest.setDescription("High-speed internet");
        activeRequest.setIsActive(true);
        amenityService.create(activeRequest);

        Page<AmenityResponseDTO> inactiveAmenities = amenityService.getAllInactive(PageRequest.of(0, 10));

        assertEquals(0, inactiveAmenities.getTotalElements());
        assertTrue(inactiveAmenities.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should list all active amenities")
    void listAllActiveAmenities() {
        // Create active amenities
        AmenityRequestDTO activeRequest1 = new AmenityRequestDTO();
        activeRequest1.setName("WiFi");
        activeRequest1.setIsActive(true);
        amenityService.create(activeRequest1);

        AmenityRequestDTO activeRequest2 = new AmenityRequestDTO();
        activeRequest2.setName("Pool");
        activeRequest2.setIsActive(true);
        amenityService.create(activeRequest2);

        // Create inactive amenity
        AmenityRequestDTO inactiveRequest = new AmenityRequestDTO();
        inactiveRequest.setName("Spa");
        inactiveRequest.setIsActive(false);
        amenityService.create(inactiveRequest);

        Page<AmenityResponseDTO> activeAmenities = amenityService.getAllActive(PageRequest.of(0, 10));

        assertEquals(2, activeAmenities.getTotalElements());
        assertTrue(activeAmenities.getContent().stream().allMatch(AmenityResponseDTO::getIsActive));
    }

    @Test
    @DisplayName("Should soft delete amenity (mark as inactive)")
    void softDeleteAmenity() {
        AmenityRequestDTO request = new AmenityRequestDTO();
        request.setName("WiFi");
        request.setIsActive(true);
        AmenityResponseDTO created = amenityService.create(request);

        amenityService.softDelete(created.getId());

        AmenityResponseDTO afterDelete = amenityService.getById(created.getId());
        assertFalse(afterDelete.getIsActive());
    }

    @Test
    @DisplayName("Should reactivate inactive amenity")
    void reactivateInactiveAmenity() {
        AmenityRequestDTO request = new AmenityRequestDTO();
        request.setName("WiFi");
        request.setIsActive(false);
        AmenityResponseDTO created = amenityService.create(request);

        amenityService.reactivate(created.getId());

        AmenityResponseDTO afterReactivate = amenityService.getById(created.getId());
        assertTrue(afterReactivate.getIsActive());
    }

    @Test
    @DisplayName("Should list all amenities (active and inactive)")
    void listAllAmenities() {
        // Create active amenity
        AmenityRequestDTO activeRequest = new AmenityRequestDTO();
        activeRequest.setName("WiFi");
        activeRequest.setIsActive(true);
        amenityService.create(activeRequest);

        // Create inactive amenity
        AmenityRequestDTO inactiveRequest = new AmenityRequestDTO();
        inactiveRequest.setName("Spa");
        inactiveRequest.setIsActive(false);
        amenityService.create(inactiveRequest);

        Page<AmenityResponseDTO> allAmenities = amenityService.getAll(PageRequest.of(0, 10));

        assertEquals(2, allAmenities.getTotalElements());
    }

    @Test
    @DisplayName("Should paginate inactive amenities")
    void paginateInactiveAmenities() {
        // Create 5 inactive amenities
        for (int i = 1; i <= 5; i++) {
            AmenityRequestDTO request = new AmenityRequestDTO();
            request.setName("Inactive Amenity " + i);
            request.setIsActive(false);
            amenityService.create(request);
        }

        // Get first page with 2 items
        Page<AmenityResponseDTO> page1 = amenityService.getAllInactive(PageRequest.of(0, 2));
        assertEquals(2, page1.getContent().size());
        assertEquals(5, page1.getTotalElements());
        assertEquals(3, page1.getTotalPages());

        // Get second page
        Page<AmenityResponseDTO> page2 = amenityService.getAllInactive(PageRequest.of(1, 2));
        assertEquals(2, page2.getContent().size());

        // Get third page
        Page<AmenityResponseDTO> page3 = amenityService.getAllInactive(PageRequest.of(2, 2));
        assertEquals(1, page3.getContent().size());
    }

    @Test
    @DisplayName("Amenity should default to active when created without isActive")
    void amenityDefaultsToActive() {
        AmenityRequestDTO request = new AmenityRequestDTO();
        request.setName("WiFi");
        // Don't set isActive

        AmenityResponseDTO created = amenityService.create(request);

        assertTrue(created.getIsActive());
    }

    @Test
    @DisplayName("Should hard delete amenity permanently")
    void hardDeleteAmenity() {
        AmenityRequestDTO request = new AmenityRequestDTO();
        request.setName("WiFi");
        AmenityResponseDTO created = amenityService.create(request);
        Long id = created.getId();

        amenityService.hardDelete(id);

        assertThrows(AmenityNotFoundException.class, () -> {
            amenityService.getById(id);
        });
    }
}
