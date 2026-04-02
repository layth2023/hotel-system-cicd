package com.Notification;

import com.Booking.Booking;
import com.Booking.BookingStatus;
import com.Hotel.Hotel;
import com.Payment.Payment;
import com.Payment.PaymentStatus;
import com.Review.Review;
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
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Hotel hotel;
    private Room room;
    private Booking booking;
    private Payment payment;
    private Review review;
    private Notification notification;
    private NotificationResponseDTO responseDTO;
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
        booking.setConfirmationNumber("BK123456");
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.now().plusDays(1));
        booking.setCheckOutDate(LocalDate.now().plusDays(3));
        booking.setStatus(BookingStatus.CONFIRMED);

        payment = new Payment();
        payment.setId(1L);
        payment.setBooking(booking);
        payment.setAmount(new BigDecimal("300.00"));
        payment.setRefundAmount(new BigDecimal("100.00"));
        payment.setStatus(PaymentStatus.PAID);

        review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setHotel(hotel);
        review.setRating(5);

        notification = new Notification();
        notification.setId(1L);
        notification.setUser(user);
        notification.setType(NotificationType.BOOKING_CREATED);
        notification.setMessage("Your booking has been created");
        notification.setRead(false);

        responseDTO = new NotificationResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setType(NotificationType.BOOKING_CREATED);
        responseDTO.setMessage("Your booking has been created");
        responseDTO.setRead(false);

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Get Notifications Tests")
    class GetTests {

        @Test
        @DisplayName("Should get user notifications")
        void shouldGetUserNotifications() {
            Page<Notification> notificationPage = new PageImpl<>(List.of(notification));
            when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L, pageable))
                    .thenReturn(notificationPage);
            when(notificationMapper.toResponseDTO(notification)).thenReturn(responseDTO);

            Page<NotificationResponseDTO> result = notificationService.getUserNotifications(1L, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should get unread notifications")
        void shouldGetUnreadNotifications() {
            Page<Notification> notificationPage = new PageImpl<>(List.of(notification));
            when(notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(1L, pageable))
                    .thenReturn(notificationPage);
            when(notificationMapper.toResponseDTO(notification)).thenReturn(responseDTO);

            Page<NotificationResponseDTO> result = notificationService.getUnreadNotifications(1L, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should get notification by id for owner")
        void shouldGetByIdForOwner() {
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
            when(notificationMapper.toResponseDTO(notification)).thenReturn(responseDTO);

            NotificationResponseDTO result = notificationService.getById(1L, 1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw exception when notification not found")
        void shouldThrowExceptionWhenNotFound() {
            when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotificationNotFoundException.class, () -> notificationService.getById(999L, 1L));
        }

        @Test
        @DisplayName("Should throw exception when accessing other user notification")
        void shouldThrowExceptionWhenAccessingOtherUserNotification() {
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

            assertThrows(NotificationNotFoundException.class, () -> notificationService.getById(1L, 999L));
        }

        @Test
        @DisplayName("Should get unread count")
        void shouldGetUnreadCount() {
            when(notificationRepository.countByUserIdAndReadFalse(1L)).thenReturn(5L);

            long result = notificationService.getUnreadCount(1L);

            assertEquals(5L, result);
        }
    }

    @Nested
    @DisplayName("Mark As Read Tests")
    class MarkAsReadTests {

        @Test
        @DisplayName("Should mark notification as read")
        void shouldMarkAsRead() {
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
            when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
            when(notificationMapper.toResponseDTO(notification)).thenReturn(responseDTO);

            NotificationResponseDTO result = notificationService.markAsRead(1L, 1L);

            assertTrue(notification.isRead());
            verify(notificationRepository).save(notification);
        }

        @Test
        @DisplayName("Should throw exception when marking other user notification")
        void shouldThrowExceptionWhenMarkingOtherUserNotification() {
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

            assertThrows(NotificationNotFoundException.class, () -> notificationService.markAsRead(1L, 999L));
        }

        @Test
        @DisplayName("Should mark all as read")
        void shouldMarkAllAsRead() {
            when(notificationRepository.markAllAsReadByUserId(1L)).thenReturn(3);

            int result = notificationService.markAllAsRead(1L);

            assertEquals(3, result);
        }
    }

    @Nested
    @DisplayName("Delete Notification Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete notification for owner")
        void shouldDeleteForOwner() {
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

            notificationService.delete(1L, 1L);

            verify(notificationRepository).delete(notification);
        }

        @Test
        @DisplayName("Should throw exception when deleting other user notification")
        void shouldThrowExceptionWhenDeletingOtherUserNotification() {
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

            assertThrows(NotificationNotFoundException.class, () -> notificationService.delete(1L, 999L));
        }
    }

    @Nested
    @DisplayName("Booking Notification Tests")
    class BookingNotificationTests {

        @Test
        @DisplayName("Should notify booking created")
        void shouldNotifyBookingCreated() {
            notificationService.notifyBookingCreated(booking);

            verify(notificationRepository).save(argThat(n ->
                    n.getType() == NotificationType.BOOKING_CREATED &&
                            n.getUser().equals(user)
            ));
        }

        @Test
        @DisplayName("Should notify booking confirmed")
        void shouldNotifyBookingConfirmed() {
            notificationService.notifyBookingConfirmed(booking);

            verify(notificationRepository).save(argThat(n ->
                    n.getType() == NotificationType.BOOKING_CONFIRMED
            ));
        }

        @Test
        @DisplayName("Should notify booking cancelled")
        void shouldNotifyBookingCancelled() {
            notificationService.notifyBookingCancelled(booking);

            verify(notificationRepository).save(argThat(n ->
                    n.getType() == NotificationType.BOOKING_CANCELLED
            ));
        }

        @Test
        @DisplayName("Should notify booking checked in")
        void shouldNotifyBookingCheckedIn() {
            notificationService.notifyBookingCheckedIn(booking);

            verify(notificationRepository).save(argThat(n ->
                    n.getType() == NotificationType.BOOKING_CHECKED_IN
            ));
        }

        @Test
        @DisplayName("Should notify booking checked out")
        void shouldNotifyBookingCheckedOut() {
            notificationService.notifyBookingCheckedOut(booking);

            verify(notificationRepository).save(argThat(n ->
                    n.getType() == NotificationType.BOOKING_CHECKED_OUT
            ));
        }
    }

    @Nested
    @DisplayName("Payment Notification Tests")
    class PaymentNotificationTests {

        @Test
        @DisplayName("Should notify payment received")
        void shouldNotifyPaymentReceived() {
            notificationService.notifyPaymentReceived(payment);

            verify(notificationRepository).save(argThat(n ->
                    n.getType() == NotificationType.PAYMENT_RECEIVED
            ));
        }

        @Test
        @DisplayName("Should notify payment failed")
        void shouldNotifyPaymentFailed() {
            notificationService.notifyPaymentFailed(payment);

            verify(notificationRepository).save(argThat(n ->
                    n.getType() == NotificationType.PAYMENT_FAILED
            ));
        }

        @Test
        @DisplayName("Should notify payment refunded")
        void shouldNotifyPaymentRefunded() {
            notificationService.notifyPaymentRefunded(payment);

            verify(notificationRepository).save(argThat(n ->
                    n.getType() == NotificationType.PAYMENT_REFUNDED
            ));
        }
    }

    @Nested
    @DisplayName("Review Notification Tests")
    class ReviewNotificationTests {

        @Test
        @DisplayName("Should notify review approved")
        void shouldNotifyReviewApproved() {
            notificationService.notifyReviewApproved(review);

            verify(notificationRepository).save(argThat(n ->
                    n.getType() == NotificationType.REVIEW_APPROVED
            ));
        }

        @Test
        @DisplayName("Should notify review responded")
        void shouldNotifyReviewResponded() {
            notificationService.notifyReviewResponded(review);

            verify(notificationRepository).save(argThat(n ->
                    n.getType() == NotificationType.REVIEW_RESPONDED
            ));
        }
    }

    @Nested
    @DisplayName("Generic Notification Tests")
    class GenericNotificationTests {

        @Test
        @DisplayName("Should send generic notification")
        void shouldSendGenericNotification() {
            notificationService.sendNotification(user, NotificationType.BOOKING_CREATED, "Test message");

            verify(notificationRepository).save(argThat(n ->
                    n.getMessage().equals("Test message") &&
                            n.getUser().equals(user)
            ));
        }

        @Test
        @DisplayName("Should send notification with reference")
        void shouldSendNotificationWithReference() {
            notificationService.sendNotification(user, NotificationType.BOOKING_CREATED, "Test message", 1L, "BOOKING");

            verify(notificationRepository).save(argThat(n ->
                    n.getReferenceId() == 1L &&
                            n.getReferenceType().equals("BOOKING")
            ));
        }
    }
}
