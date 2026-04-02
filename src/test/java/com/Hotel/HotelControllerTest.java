package com.Hotel;

import com.Security.CustomUserDetailsService;
import com.Security.JwtTokenProvider;
import com.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
@Import(TestSecurityConfig.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HotelService hotelService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private HotelRequestDTO requestDTO;
    private HotelResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new HotelRequestDTO();
        requestDTO.setName("Test Hotel");
        requestDTO.setAddress("123 Test Street");
        requestDTO.setCity("Test City");
        requestDTO.setCountry("Test Country");

        responseDTO = new HotelResponseDTO(
                1L, "Test Hotel", "123 Test Street", "Test City",
                "Test Country", null, null, null, Set.of()
        );
    }

    @Nested
    @DisplayName("Create Hotel Endpoint Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create hotel with admin role")
        @WithMockUser(roles = "ADMIN")
        void shouldCreateHotelWithAdminRole() throws Exception {
            when(hotelService.create(any(HotelRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/hotels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Test Hotel"));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn400WhenNameBlank() throws Exception {
            requestDTO.setName("");

            mockMvc.perform(post("/hotels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when address is blank")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn400WhenAddressBlank() throws Exception {
            requestDTO.setAddress("");

            mockMvc.perform(post("/hotels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when hotel already exists")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn409WhenHotelExists() throws Exception {
            when(hotelService.create(any(HotelRequestDTO.class)))
                    .thenThrow(new HotelAlreadyExistsException("Test Hotel"));

            mockMvc.perform(post("/hotels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("Get Hotel Endpoint Tests")
    class GetTests {

        @Test
        @DisplayName("Should get hotel by id")
        @WithMockUser(roles = "USER")
        void shouldGetHotelById() throws Exception {
            when(hotelService.getById(1L)).thenReturn(responseDTO);

            mockMvc.perform(get("/hotels/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Test Hotel"));
        }

        @Test
        @DisplayName("Should return 404 when hotel not found")
        @WithMockUser(roles = "USER")
        void shouldReturn404WhenNotFound() throws Exception {
            when(hotelService.getById(999L)).thenThrow(new HotelNotFoundException(999L));

            mockMvc.perform(get("/hotels/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should get all hotels paginated")
        @WithMockUser(roles = "USER")
        void shouldGetAllHotelsPaginated() throws Exception {
            Page<HotelResponseDTO> page = new PageImpl<>(List.of(responseDTO));
            when(hotelService.getAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/hotels")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].name").value("Test Hotel"));
        }
    }

    @Nested
    @DisplayName("Update Hotel Endpoint Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update hotel with admin role")
        @WithMockUser(roles = "ADMIN")
        void shouldUpdateHotelWithAdminRole() throws Exception {
            when(hotelService.update(eq(1L), any(HotelRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(put("/hotels/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Test Hotel"));
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent hotel")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn404WhenUpdatingNonExistent() throws Exception {
            when(hotelService.update(eq(999L), any(HotelRequestDTO.class)))
                    .thenThrow(new HotelNotFoundException(999L));

            mockMvc.perform(put("/hotels/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Delete Hotel Endpoint Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete hotel with admin role")
        @WithMockUser(roles = "ADMIN")
        void shouldDeleteHotelWithAdminRole() throws Exception {
            doNothing().when(hotelService).delete(1L);

            mockMvc.perform(delete("/hotels/1"))
                    .andExpect(status().isNoContent());

            verify(hotelService).delete(1L);
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent hotel")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn404WhenDeletingNonExistent() throws Exception {
            doThrow(new HotelNotFoundException(999L)).when(hotelService).delete(999L);

            mockMvc.perform(delete("/hotels/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Amenity Management Endpoint Tests")
    class AmenityTests {

        @Test
        @DisplayName("Should add amenity to hotel")
        @WithMockUser(roles = "ADMIN")
        void shouldAddAmenityToHotel() throws Exception {
            doNothing().when(hotelService).addAmenity(1L, 1L);

            mockMvc.perform(post("/hotels/1/amenities/1"))
                    .andExpect(status().isNoContent());

            verify(hotelService).addAmenity(1L, 1L);
        }

        @Test
        @DisplayName("Should remove amenity from hotel")
        @WithMockUser(roles = "ADMIN")
        void shouldRemoveAmenityFromHotel() throws Exception {
            doNothing().when(hotelService).removeAmenity(1L, 1L);

            mockMvc.perform(delete("/hotels/1/amenities/1"))
                    .andExpect(status().isNoContent());

            verify(hotelService).removeAmenity(1L, 1L);
        }
    }

    @Nested
    @DisplayName("Search Hotels Endpoint Tests")
    class SearchTests {

        @Test
        @DisplayName("Should search hotels by city")
        @WithMockUser(roles = "USER")
        void shouldSearchHotelsByCity() throws Exception {
            Page<HotelResponseDTO> page = new PageImpl<>(List.of(responseDTO));
            when(hotelService.searchHotels(eq("Test City"), isNull(), isNull(), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/hotels/search")
                            .param("city", "Test City"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].city").value("Test City"));
        }

        @Test
        @DisplayName("Should search hotels by country and star rating")
        @WithMockUser(roles = "USER")
        void shouldSearchByCountryAndStarRating() throws Exception {
            Page<HotelResponseDTO> page = new PageImpl<>(List.of(responseDTO));
            when(hotelService.searchHotels(isNull(), eq("Test Country"), eq(4), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/hotels/search")
                            .param("country", "Test Country")
                            .param("minStarRating", "4"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Hotel Rooms Endpoint Tests")
    class HotelRoomsTests {

        @Test
        @DisplayName("Should get hotel rooms")
        @WithMockUser(roles = "USER")
        void shouldGetHotelRooms() throws Exception {
            when(hotelService.getHotelRooms(1L)).thenReturn(List.of());

            mockMvc.perform(get("/hotels/1/rooms"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("Should get available rooms with date filter")
        @WithMockUser(roles = "USER")
        void shouldGetAvailableRooms() throws Exception {
            when(hotelService.getHotelAvailableRooms(eq(1L), any(), any(), any()))
                    .thenReturn(List.of());

            mockMvc.perform(get("/hotels/1/rooms/available")
                            .param("checkInDate", "2025-06-01")
                            .param("checkOutDate", "2025-06-05")
                            .param("guests", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }
}
