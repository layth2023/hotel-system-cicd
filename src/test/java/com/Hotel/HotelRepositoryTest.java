package com.Hotel;

import com.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
@DisplayName("Hotel Repository Tests")
class HotelRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel hotel1;
    private Hotel hotel2;
    private Hotel hotel3;

    @BeforeEach
    void setUp() {
        hotelRepository.deleteAll();

        hotel1 = new Hotel();
        hotel1.setName("Grand Hotel");
        hotel1.setAddress("123 Main St");
        hotel1.setCity("New York");
        hotel1.setCountry("USA");
        hotel1.setStarRating(5);
        hotel1.setAmenities(new HashSet<>());
        hotel1 = hotelRepository.save(hotel1);

        hotel2 = new Hotel();
        hotel2.setName("Beach Resort");
        hotel2.setAddress("456 Ocean Ave");
        hotel2.setCity("Miami");
        hotel2.setCountry("USA");
        hotel2.setStarRating(4);
        hotel2.setAmenities(new HashSet<>());
        hotel2 = hotelRepository.save(hotel2);

        hotel3 = new Hotel();
        hotel3.setName("Mountain Lodge");
        hotel3.setAddress("789 Alpine Rd");
        hotel3.setCity("Denver");
        hotel3.setCountry("USA");
        hotel3.setStarRating(3);
        hotel3.setAmenities(new HashSet<>());
        hotel3 = hotelRepository.save(hotel3);
    }

    @Nested
    @DisplayName("Name Existence Tests")
    class NameExistenceTests {

        @Test
        @DisplayName("Should return true when name exists (case insensitive)")
        void shouldReturnTrueWhenNameExists() {
            assertTrue(hotelRepository.existsByNameInsensitive("Grand Hotel"));
            assertTrue(hotelRepository.existsByNameInsensitive("grand hotel"));
            assertTrue(hotelRepository.existsByNameInsensitive("GRAND HOTEL"));
        }

        @Test
        @DisplayName("Should return false when name does not exist")
        void shouldReturnFalseWhenNameDoesNotExist() {
            assertFalse(hotelRepository.existsByNameInsensitive("Nonexistent Hotel"));
        }

        @Test
        @DisplayName("Should check name exists excluding specific id")
        void shouldCheckNameExistsExcludingId() {
            assertTrue(hotelRepository.existsByNameInsensitiveAndIdNot("Beach Resort", hotel1.getId()));
            assertFalse(hotelRepository.existsByNameInsensitiveAndIdNot("Beach Resort", hotel2.getId()));
        }
    }

    @Nested
    @DisplayName("Search Hotels Tests")
    class SearchTests {

        @Test
        @DisplayName("Should search by city")
        void shouldSearchByCity() {
            Page<Hotel> result = hotelRepository.searchHotels(
                    "New York", null, null, PageRequest.of(0, 10));

            assertEquals(1, result.getTotalElements());
            assertEquals("Grand Hotel", result.getContent().get(0).getName());
        }

        @Test
        @DisplayName("Should search by partial city name")
        void shouldSearchByPartialCity() {
            Page<Hotel> result = hotelRepository.searchHotels(
                    "new", null, null, PageRequest.of(0, 10));

            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should search by country")
        void shouldSearchByCountry() {
            Page<Hotel> result = hotelRepository.searchHotels(
                    null, "USA", null, PageRequest.of(0, 10));

            assertEquals(3, result.getTotalElements());
        }

        @Test
        @DisplayName("Should search by minimum star rating")
        void shouldSearchByMinStarRating() {
            Page<Hotel> result = hotelRepository.searchHotels(
                    null, null, 4, PageRequest.of(0, 10));

            assertEquals(2, result.getTotalElements());
        }

        @Test
        @DisplayName("Should search by city and star rating")
        void shouldSearchByCityAndStarRating() {
            Page<Hotel> result = hotelRepository.searchHotels(
                    "New York", null, 4, PageRequest.of(0, 10));

            assertEquals(1, result.getTotalElements());
            assertEquals("Grand Hotel", result.getContent().get(0).getName());
        }

        @Test
        @DisplayName("Should return all when no filters")
        void shouldReturnAllWhenNoFilters() {
            Page<Hotel> result = hotelRepository.searchHotels(
                    null, null, null, PageRequest.of(0, 10));

            assertEquals(3, result.getTotalElements());
        }

        @Test
        @DisplayName("Should return empty when no matches")
        void shouldReturnEmptyWhenNoMatches() {
            Page<Hotel> result = hotelRepository.searchHotels(
                    "Paris", null, null, PageRequest.of(0, 10));

            assertEquals(0, result.getTotalElements());
        }

        @Test
        @DisplayName("Should search case insensitively")
        void shouldSearchCaseInsensitively() {
            Page<Hotel> result = hotelRepository.searchHotels(
                    "NEW YORK", null, null, PageRequest.of(0, 10));

            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {

        @Test
        @DisplayName("Should paginate results")
        void shouldPaginateResults() {
            Page<Hotel> firstPage = hotelRepository.searchHotels(
                    null, "USA", null, PageRequest.of(0, 2));

            assertEquals(2, firstPage.getContent().size());
            assertEquals(3, firstPage.getTotalElements());
            assertEquals(2, firstPage.getTotalPages());
            assertFalse(firstPage.isLast());
        }

        @Test
        @DisplayName("Should return last page correctly")
        void shouldReturnLastPageCorrectly() {
            Page<Hotel> lastPage = hotelRepository.searchHotels(
                    null, "USA", null, PageRequest.of(1, 2));

            assertEquals(1, lastPage.getContent().size());
            assertTrue(lastPage.isLast());
        }
    }
}
