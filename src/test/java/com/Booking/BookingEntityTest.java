package com.Booking;

import com.Hotel.Hotel;
import com.Payment.Payment;
import com.Payment.PaymentStatus;
import com.Room.Room;
import com.RoomType.RoomType;
import com.User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Booking Entity Tests")
class BookingEntityTest {

    private User user;
    private Hotel hotel;
    private RoomType roomType;
    private Room room;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        hotel.setAmenities(new HashSet<>());

        roomType = new RoomType();
        roomType.setId(1L);
        roomType.setCapacity(4);
        roomType.setPricePerNight(new BigDecimal("150.00"));

        room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setHotel(hotel);
        room.setRoomType(roomType);

        booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.now().plusDays(1));
        booking.setCheckOutDate(LocalDate.now().plusDays(4));
        booking.setNumberOfGuests(2);
        booking.setNumberOfAdults(2);
        booking.setPricePerNight(new BigDecimal("150.00"));
        booking.setStatus(BookingStatus.PENDING);
    }

    @Nested
    @DisplayName("Number of Nights Calculation Tests")
    class NightsCalculationTests {

        @Test
        @DisplayName("Should calculate number of nights correctly")
        void shouldCalculateNightsCorrectly() {
            assertEquals(3, booking.getNumberOfNights());
        }

        @Test
        @DisplayName("Should calculate single night stay")
        void shouldCalculateSingleNight() {
            booking.setCheckOutDate(booking.getCheckInDate().plusDays(1));
            assertEquals(1, booking.getNumberOfNights());
        }

        @Test
        @DisplayName("Should calculate long stay")
        void shouldCalculateLongStay() {
            booking.setCheckOutDate(booking.getCheckInDate().plusDays(30));
            assertEquals(30, booking.getNumberOfNights());
        }
    }

    @Nested
    @DisplayName("Total Price Calculation Tests")
    class TotalPriceCalculationTests {

        @Test
        @DisplayName("Should calculate total price without extras")
        void shouldCalculateTotalPriceWithoutExtras() {
            booking.setTotalPrice(booking.calculateTotalPrice());

            assertEquals(new BigDecimal("450.00"), booking.getTotalPrice());
        }

        @Test
        @DisplayName("Should calculate total price with discount")
        void shouldCalculateTotalPriceWithDiscount() {
            booking.setDiscount(new BigDecimal("50.00"));
            booking.setTotalPrice(booking.calculateTotalPrice());

            assertEquals(new BigDecimal("400.00"), booking.getTotalPrice());
        }

        @Test
        @DisplayName("Should calculate total price with tax")
        void shouldCalculateTotalPriceWithTax() {
            booking.setTaxAmount(new BigDecimal("45.00"));
            booking.setTotalPrice(booking.calculateTotalPrice());

            assertEquals(new BigDecimal("495.00"), booking.getTotalPrice());
        }

        @Test
        @DisplayName("Should calculate total price with amenities")
        void shouldCalculateTotalPriceWithAmenities() {
            booking.setAmenityTotal(new BigDecimal("10.00"));
            booking.setTotalPrice(booking.calculateTotalPrice());

            assertEquals(new BigDecimal("480.00"), booking.getTotalPrice());
        }

        @Test
        @DisplayName("Should calculate total price with all extras")
        void shouldCalculateTotalPriceWithAllExtras() {
            booking.setDiscount(new BigDecimal("50.00"));
            booking.setTaxAmount(new BigDecimal("45.00"));
            booking.setAmenityTotal(new BigDecimal("10.00"));
            booking.setTotalPrice(booking.calculateTotalPrice());

            assertEquals(new BigDecimal("475.00"), booking.getTotalPrice());
        }

        @Test
        @DisplayName("Should return zero when price per night is null")
        void shouldReturnZeroWhenPricePerNightNull() {
            booking.setPricePerNight(null);
            assertEquals(BigDecimal.ZERO, booking.calculateTotalPrice());
        }
    }

    @Nested
    @DisplayName("Payment Tests")
    class PaymentTests {

        @Test
        @DisplayName("Should add payment to booking")
        void shouldAddPaymentToBooking() {
            Payment payment = new Payment();
            payment.setAmount(new BigDecimal("100.00"));

            booking.addPayment(payment);

            assertEquals(1, booking.getPayments().size());
            assertEquals(booking, payment.getBooking());
        }

        @Test
        @DisplayName("Should remove payment from booking")
        void shouldRemovePaymentFromBooking() {
            Payment payment = new Payment();
            booking.addPayment(payment);
            booking.removePayment(payment);

            assertEquals(0, booking.getPayments().size());
            assertNull(payment.getBooking());
        }

        @Test
        @DisplayName("Should calculate total paid")
        void shouldCalculateTotalPaid() {
            booking.setTotalPrice(new BigDecimal("300.00"));

            Payment payment1 = new Payment();
            payment1.setAmount(new BigDecimal("100.00"));
            payment1.setStatus(PaymentStatus.PAID);
            booking.addPayment(payment1);

            Payment payment2 = new Payment();
            payment2.setAmount(new BigDecimal("150.00"));
            payment2.setStatus(PaymentStatus.PAID);
            booking.addPayment(payment2);

            assertEquals(new BigDecimal("250.00"), booking.getTotalPaid());
        }

        @Test
        @DisplayName("Should calculate balance due")
        void shouldCalculateBalanceDue() {
            booking.setTotalPrice(new BigDecimal("300.00"));

            Payment payment = new Payment();
            payment.setAmount(new BigDecimal("100.00"));
            payment.setStatus(PaymentStatus.PAID);
            booking.addPayment(payment);

            assertEquals(new BigDecimal("200.00"), booking.getBalanceDue());
        }

        @Test
        @DisplayName("Should check if fully paid")
        void shouldCheckIfFullyPaid() {
            booking.setTotalPrice(new BigDecimal("300.00"));

            Payment payment = new Payment();
            payment.setAmount(new BigDecimal("300.00"));
            payment.setStatus(PaymentStatus.PAID);
            booking.addPayment(payment);

            assertTrue(booking.isFullyPaid());
        }

        @Test
        @DisplayName("Should return false when not fully paid")
        void shouldReturnFalseWhenNotFullyPaid() {
            booking.setTotalPrice(new BigDecimal("300.00"));

            Payment payment = new Payment();
            payment.setAmount(new BigDecimal("100.00"));
            payment.setStatus(PaymentStatus.PAID);
            booking.addPayment(payment);

            assertFalse(booking.isFullyPaid());
        }
    }

    @Nested
    @DisplayName("Cancellation Tests")
    class CancellationTests {

        @Test
        @DisplayName("Should be cancellable when pending")
        void shouldBeCancellableWhenPending() {
            booking.setStatus(BookingStatus.PENDING);
            assertTrue(booking.isCancellable());
        }

        @Test
        @DisplayName("Should be cancellable when confirmed")
        void shouldBeCancellableWhenConfirmed() {
            booking.setStatus(BookingStatus.CONFIRMED);
            assertTrue(booking.isCancellable());
        }

        @Test
        @DisplayName("Should not be cancellable when checked in")
        void shouldNotBeCancellableWhenCheckedIn() {
            booking.setStatus(BookingStatus.CHECKED_IN);
            assertFalse(booking.isCancellable());
        }

        @Test
        @DisplayName("Should not be cancellable when checked out")
        void shouldNotBeCancellableWhenCheckedOut() {
            booking.setStatus(BookingStatus.CHECKED_OUT);
            assertFalse(booking.isCancellable());
        }

        @Test
        @DisplayName("Should not be cancellable when cancelled")
        void shouldNotBeCancellableWhenCancelled() {
            booking.setStatus(BookingStatus.CANCELLED);
            assertFalse(booking.isCancellable());
        }

        @Test
        @DisplayName("Should not be cancellable when no-show")
        void shouldNotBeCancellableWhenNoShow() {
            booking.setStatus(BookingStatus.NO_SHOW);
            assertFalse(booking.isCancellable());
        }
    }

    @Nested
    @DisplayName("Equality Tests")
    class EqualityTests {

        @Test
        @DisplayName("Should be equal when same id")
        void shouldBeEqualWhenSameId() {
            Booking booking2 = new Booking();
            booking2.setId(1L);

            assertEquals(booking, booking2);
        }

        @Test
        @DisplayName("Should not be equal when different id")
        void shouldNotBeEqualWhenDifferentId() {
            Booking booking2 = new Booking();
            booking2.setId(2L);

            assertNotEquals(booking, booking2);
        }

        @Test
        @DisplayName("Should have same hash code for same id")
        void shouldHaveSameHashCodeForSameId() {
            Booking booking2 = new Booking();
            booking2.setId(1L);

            assertEquals(booking.hashCode(), booking2.hashCode());
        }
    }
}
