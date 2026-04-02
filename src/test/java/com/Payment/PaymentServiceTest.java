package com.Payment;

import com.Booking.Booking;
import com.Booking.BookingNotFoundException;
import com.Booking.BookingRepository;
import com.Booking.BookingStatus;
import com.Hotel.Hotel;
import com.Notification.NotificationService;
import com.PaymentTransaction.PaymentTransactionRepository;
import com.Room.Room;
import com.RoomType.RoomType;
import com.User.User;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentTransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Hotel hotel;
    private Room room;
    private Booking booking;
    private Payment payment;
    private PaymentRequestDTO requestDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        hotel.setAmenities(new HashSet<>());

        RoomType roomType = new RoomType();
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
        booking.setCheckOutDate(LocalDate.now().plusDays(3));
        booking.setTotalPrice(new BigDecimal("300.00"));
        booking.setStatus(BookingStatus.CONFIRMED);

        payment = new Payment();
        payment.setId(1L);
        payment.setBooking(booking);
        payment.setAmount(new BigDecimal("300.00"));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        requestDTO = new PaymentRequestDTO();
        requestDTO.setBookingId(1L);
        requestDTO.setAmount(new BigDecimal("300.00"));

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Create Payment Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create payment successfully")
        void shouldCreatePayment() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            PaymentResponseDTO result = paymentService.create(requestDTO);

            assertNotNull(result);
            assertEquals(new BigDecimal("300.00"), result.getAmount());
        }

        @Test
        @DisplayName("Should throw exception when booking not found")
        void shouldThrowExceptionWhenBookingNotFound() {
            when(bookingRepository.findById(999L)).thenReturn(Optional.empty());
            requestDTO.setBookingId(999L);

            assertThrows(BookingNotFoundException.class, () -> paymentService.create(requestDTO));
        }
    }

    @Nested
    @DisplayName("Mark As Paid Tests")
    class MarkAsPaidTests {

        @Test
        @DisplayName("Should mark payment as paid")
        void shouldMarkAsPaid() {
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            PaymentResponseDTO result = paymentService.markAsPaid(1L, "TXN123");

            assertNotNull(result);
            assertEquals(PaymentStatus.PAID, payment.getStatus());
            verify(notificationService).notifyPaymentReceived(any(Payment.class));
        }

        @Test
        @DisplayName("Should mark payment as paid with auto-generated transaction id")
        void shouldMarkAsPaidWithAutoTransactionId() {
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            PaymentResponseDTO result = paymentService.markAsPaid(1L);

            assertNotNull(result);
            assertNotNull(payment.getTransactionId());
        }

        @Test
        @DisplayName("Should throw exception when payment not found")
        void shouldThrowExceptionWhenPaymentNotFound() {
            when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(PaymentNotFoundException.class, () -> paymentService.markAsPaid(999L, "TXN123"));
        }
    }

    @Nested
    @DisplayName("Cancel Payment Tests")
    class CancelTests {

        @Test
        @DisplayName("Should cancel pending payment")
        void shouldCancelPendingPayment() {
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            PaymentResponseDTO result = paymentService.cancel(1L);

            assertNotNull(result);
            assertEquals(PaymentStatus.CANCELLED, payment.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when cancelling paid payment")
        void shouldThrowExceptionWhenCancellingPaidPayment() {
            payment.setStatus(PaymentStatus.PAID);
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            assertThrows(PaymentBadRequestException.class, () -> paymentService.cancel(1L));
        }
    }

    @Nested
    @DisplayName("Get Payment Tests")
    class GetTests {

        @Test
        @DisplayName("Should get payment by id")
        void shouldGetById() {
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            PaymentResponseDTO result = paymentService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw exception when payment not found")
        void shouldThrowExceptionWhenNotFound() {
            when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(PaymentNotFoundException.class, () -> paymentService.getById(999L));
        }

        @Test
        @DisplayName("Should find payments by booking")
        void shouldFindByBooking() {
            when(paymentRepository.findByBookingId(1L)).thenReturn(List.of(payment));

            List<PaymentResponseDTO> result = paymentService.findByBooking(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Refund Tests")
    class RefundTests {

        @Test
        @DisplayName("Should process full refund")
        void shouldProcessFullRefund() {
            payment.setStatus(PaymentStatus.PAID);
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            PaymentResponseDTO result = paymentService.refund(1L, "Customer request");

            assertNotNull(result);
            assertEquals(PaymentStatus.REFUNDED, payment.getStatus());
            verify(notificationService).notifyPaymentRefunded(any(Payment.class));
        }

        @Test
        @DisplayName("Should throw exception when refunding non-refundable payment")
        void shouldThrowExceptionWhenRefundingNonRefundable() {
            payment.setStatus(PaymentStatus.PENDING);
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            assertThrows(PaymentBadRequestException.class, () -> paymentService.refund(1L, "reason"));
        }

        @Test
        @DisplayName("Should process partial refund")
        void shouldProcessPartialRefund() {
            payment.setStatus(PaymentStatus.PAID);
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            PaymentResponseDTO result = paymentService.partialRefund(1L, new BigDecimal("100.00"), "Partial refund");

            assertNotNull(result);
            assertEquals(PaymentStatus.PARTIALLY_REFUNDED, payment.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when partial refund exceeds payment amount")
        void shouldThrowExceptionWhenPartialRefundExceedsAmount() {
            payment.setStatus(PaymentStatus.PAID);
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            assertThrows(PaymentBadRequestException.class, () ->
                    paymentService.partialRefund(1L, new BigDecimal("500.00"), "reason"));
        }
    }

    @Nested
    @DisplayName("Payment Summary Tests")
    class SummaryTests {

        @Test
        @DisplayName("Should get booking payment summary")
        void shouldGetBookingPaymentSummary() {
            payment.setStatus(PaymentStatus.PAID);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(paymentRepository.findByBookingId(1L)).thenReturn(List.of(payment));

            PaymentSummaryDTO result = paymentService.getBookingPaymentSummary(1L);

            assertNotNull(result);
            assertEquals(1L, result.getBookingId());
            assertEquals(new BigDecimal("300.00"), result.getTotalPaid());
            assertTrue(result.isFullyPaid());
        }

        @Test
        @DisplayName("Should throw exception when booking not found for summary")
        void shouldThrowExceptionWhenBookingNotFoundForSummary() {
            when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(BookingNotFoundException.class, () -> paymentService.getBookingPaymentSummary(999L));
        }

        @Test
        @DisplayName("Should calculate balance due correctly")
        void shouldCalculateBalanceDueCorrectly() {
            payment.setStatus(PaymentStatus.PAID);
            payment.setAmount(new BigDecimal("100.00"));
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(paymentRepository.findByBookingId(1L)).thenReturn(List.of(payment));

            PaymentSummaryDTO result = paymentService.getBookingPaymentSummary(1L);

            assertEquals(new BigDecimal("200.00"), result.getBalanceDue());
            assertFalse(result.isFullyPaid());
        }
    }

    @Nested
    @DisplayName("Payment Transactions Tests")
    class TransactionsTests {

        @Test
        @DisplayName("Should get payment transactions")
        void shouldGetPaymentTransactions() {
            when(paymentRepository.existsById(1L)).thenReturn(true);
            when(transactionRepository.findByPaymentId(1L)).thenReturn(List.of());

            var result = paymentService.getPaymentTransactions(1L);

            assertNotNull(result);
            verify(transactionRepository).findByPaymentId(1L);
        }

        @Test
        @DisplayName("Should throw exception when payment not found for transactions")
        void shouldThrowExceptionWhenPaymentNotFoundForTransactions() {
            when(paymentRepository.existsById(999L)).thenReturn(false);

            assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentTransactions(999L));
        }
    }
}
