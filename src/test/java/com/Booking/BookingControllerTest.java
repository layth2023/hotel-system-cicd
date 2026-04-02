package com.Booking;

import com.Security.CustomUserDetails;
import com.Security.CustomUserDetailsService;
import com.Security.JwtTokenProvider;
import com.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@Import(TestSecurityConfig.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private BookingRequestDTO requestDTO;
    private BookingResponseDTO responseDTO;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        requestDTO = new BookingRequestDTO();
        requestDTO.setRoomId(1L);
        requestDTO.setCheckInDate(LocalDate.now().plusDays(1));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(3));
        requestDTO.setNumberOfGuests(2);
        requestDTO.setNumberOfAdults(2);
        requestDTO.setNumberOfChildren(0);

        responseDTO = new BookingResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setConfirmationNumber("BK123456");
        responseDTO.setStatus(BookingStatus.PENDING);
        responseDTO.setTotalPrice(new BigDecimal("300.00"));

        userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("testuser");
    }

    @Nested
    @DisplayName("Create Booking Endpoint Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create booking")
        @WithMockUser(roles = "USER")
        void shouldCreateBooking() throws Exception {
            when(bookingService.create(any(BookingRequestDTO.class), anyLong())).thenReturn(responseDTO);

            mockMvc.perform(post("/bookings")
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.confirmationNumber").value("BK123456"));
        }

        @Test
        @DisplayName("Should return 400 when room id is null")
        @WithMockUser(roles = "USER")
        void shouldReturn400WhenRoomIdNull() throws Exception {
            requestDTO.setRoomId(null);

            mockMvc.perform(post("/bookings")
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when guests less than 1")
        @WithMockUser(roles = "USER")
        void shouldReturn400WhenGuestsLessThan1() throws Exception {
            requestDTO.setNumberOfGuests(0);

            mockMvc.perform(post("/bookings")
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get Booking Endpoint Tests")
    class GetTests {

        @Test
        @DisplayName("Should get booking by id")
        @WithMockUser(roles = "USER")
        void shouldGetBookingById() throws Exception {
            when(bookingService.getById(1L)).thenReturn(responseDTO);

            mockMvc.perform(get("/bookings/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.confirmationNumber").value("BK123456"));
        }

        @Test
        @DisplayName("Should return 404 when booking not found")
        @WithMockUser(roles = "USER")
        void shouldReturn404WhenNotFound() throws Exception {
            when(bookingService.getById(999L)).thenThrow(new BookingNotFoundException(999L));

            mockMvc.perform(get("/bookings/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should get booking by confirmation number")
        @WithMockUser(roles = "USER")
        void shouldGetByConfirmationNumber() throws Exception {
            when(bookingService.getByConfirmationNumber("BK123456")).thenReturn(responseDTO);

            mockMvc.perform(get("/bookings/confirmation/BK123456"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.confirmationNumber").value("BK123456"));
        }

        @Test
        @DisplayName("Should get my bookings")
        @WithMockUser(roles = "USER")
        void shouldGetMyBookings() throws Exception {
            Page<BookingResponseDTO> page = new PageImpl<>(List.of(responseDTO));
            when(bookingService.getByUserId(anyLong(), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/bookings")
                            .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("Should get upcoming bookings")
        @WithMockUser(roles = "USER")
        void shouldGetUpcomingBookings() throws Exception {
            Page<BookingResponseDTO> page = new PageImpl<>(List.of(responseDTO));
            when(bookingService.getUpcomingByUserId(anyLong(), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/bookings/upcoming")
                            .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }
    }

    @Nested
    @DisplayName("Admin/Manager Query Endpoint Tests")
    class AdminQueryTests {

        @Test
        @DisplayName("Should get bookings by room id")
        @WithMockUser(roles = "ADMIN")
        void shouldGetByRoomId() throws Exception {
            Page<BookingResponseDTO> page = new PageImpl<>(List.of(responseDTO));
            when(bookingService.getByRoomId(eq(1L), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/bookings/room/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("Should get bookings by hotel id")
        @WithMockUser(roles = "MANAGER")
        void shouldGetByHotelId() throws Exception {
            Page<BookingResponseDTO> page = new PageImpl<>(List.of(responseDTO));
            when(bookingService.getByHotelId(eq(1L), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/bookings/hotel/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("Should get bookings by status")
        @WithMockUser(roles = "ADMIN")
        void shouldGetByStatus() throws Exception {
            Page<BookingResponseDTO> page = new PageImpl<>(List.of(responseDTO));
            when(bookingService.getByStatus(eq(BookingStatus.PENDING), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/bookings/status/PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("Should get today check-ins")
        @WithMockUser(roles = "MANAGER")
        void shouldGetTodayCheckIns() throws Exception {
            when(bookingService.getTodayCheckIns()).thenReturn(List.of(responseDTO));

            mockMvc.perform(get("/bookings/today/check-ins"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("Should get today check-outs")
        @WithMockUser(roles = "MANAGER")
        void shouldGetTodayCheckOuts() throws Exception {
            when(bookingService.getTodayCheckOuts()).thenReturn(List.of(responseDTO));

            mockMvc.perform(get("/bookings/today/check-outs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("Update Booking Endpoint Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update booking")
        @WithMockUser(roles = "USER")
        void shouldUpdateBooking() throws Exception {
            when(bookingService.update(eq(1L), any(BookingRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(put("/bookings/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.confirmationNumber").value("BK123456"));
        }
    }

    @Nested
    @DisplayName("Delete Booking Endpoint Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete booking")
        @WithMockUser(roles = "ADMIN")
        void shouldDeleteBooking() throws Exception {
            doNothing().when(bookingService).delete(1L);

            mockMvc.perform(delete("/bookings/1"))
                    .andExpect(status().isNoContent());

            verify(bookingService).delete(1L);
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn404WhenDeletingNonExistent() throws Exception {
            doThrow(new BookingNotFoundException(999L)).when(bookingService).delete(999L);

            mockMvc.perform(delete("/bookings/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Status Change Endpoint Tests")
    class StatusChangeTests {

        @Test
        @DisplayName("Should cancel booking")
        @WithMockUser(roles = "USER")
        void shouldCancelBooking() throws Exception {
            responseDTO.setStatus(BookingStatus.CANCELLED);
            when(bookingService.cancel(eq(1L), anyString())).thenReturn(responseDTO);

            mockMvc.perform(patch("/bookings/1/cancel")
                            .param("reason", "Change of plans"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CANCELLED"));
        }

        @Test
        @DisplayName("Should check-in booking")
        @WithMockUser(roles = "MANAGER")
        void shouldCheckInBooking() throws Exception {
            responseDTO.setStatus(BookingStatus.CHECKED_IN);
            when(bookingService.checkIn(1L)).thenReturn(responseDTO);

            mockMvc.perform(patch("/bookings/1/check-in"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CHECKED_IN"));
        }

        @Test
        @DisplayName("Should check-out booking")
        @WithMockUser(roles = "MANAGER")
        void shouldCheckOutBooking() throws Exception {
            responseDTO.setStatus(BookingStatus.CHECKED_OUT);
            when(bookingService.checkOut(1L)).thenReturn(responseDTO);

            mockMvc.perform(patch("/bookings/1/check-out"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CHECKED_OUT"));
        }

        @Test
        @DisplayName("Should mark no-show")
        @WithMockUser(roles = "MANAGER")
        void shouldMarkNoShow() throws Exception {
            responseDTO.setStatus(BookingStatus.NO_SHOW);
            when(bookingService.markNoShow(1L)).thenReturn(responseDTO);

            mockMvc.perform(patch("/bookings/1/no-show"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("NO_SHOW"));
        }

        @Test
        @DisplayName("Should confirm booking")
        @WithMockUser(roles = "MANAGER")
        void shouldConfirmBooking() throws Exception {
            responseDTO.setStatus(BookingStatus.CONFIRMED);
            when(bookingService.confirm(1L)).thenReturn(responseDTO);

            mockMvc.perform(patch("/bookings/1/confirm"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CONFIRMED"));
        }
    }
}
