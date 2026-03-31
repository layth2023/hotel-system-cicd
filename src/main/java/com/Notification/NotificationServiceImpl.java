package com.Notification;

import com.Booking.Booking;
import com.Payment.Payment;
import com.Review.Review;
import com.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getUnreadNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDTO getById(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        if (!notification.getUser().getId().equals(userId)) {
            throw new NotificationNotFoundException("Notification not found");
        }

        return notificationMapper.toResponseDTO(notification);
    }

    @Override
    public NotificationResponseDTO markAsRead(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        if (!notification.getUser().getId().equals(userId)) {
            throw new NotificationNotFoundException("Notification not found");
        }

        notification.setRead(true);
        notification = notificationRepository.save(notification);
        return notificationMapper.toResponseDTO(notification);
    }

    @Override
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    public void delete(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        if (!notification.getUser().getId().equals(userId)) {
            throw new NotificationNotFoundException("Notification not found");
        }

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    // ==========================================================
    // Auto-generation methods for Booking events
    // ==========================================================

    @Override
    public void notifyBookingCreated(Booking booking) {
        String message = String.format(
                "Your booking at %s has been created. Confirmation: %s. Check-in: %s.",
                booking.getRoom().getHotel().getName(),
                booking.getConfirmationNumber(),
                booking.getCheckInDate().format(DATE_FORMATTER)
        );
        sendNotification(
                booking.getUser(),
                NotificationType.BOOKING_CREATED,
                message,
                booking.getId(),
                "BOOKING"
        );
    }

    @Override
    public void notifyBookingConfirmed(Booking booking) {
        String message = String.format(
                "Your booking at %s is now confirmed! Confirmation: %s. Check-in: %s.",
                booking.getRoom().getHotel().getName(),
                booking.getConfirmationNumber(),
                booking.getCheckInDate().format(DATE_FORMATTER)
        );
        sendNotification(
                booking.getUser(),
                NotificationType.BOOKING_CONFIRMED,
                message,
                booking.getId(),
                "BOOKING"
        );
    }

    @Override
    public void notifyBookingCancelled(Booking booking) {
        String message = String.format(
                "Your booking at %s (Confirmation: %s) has been cancelled.",
                booking.getRoom().getHotel().getName(),
                booking.getConfirmationNumber()
        );
        sendNotification(
                booking.getUser(),
                NotificationType.BOOKING_CANCELLED,
                message,
                booking.getId(),
                "BOOKING"
        );
    }

    @Override
    public void notifyBookingCheckedIn(Booking booking) {
        String message = String.format(
                "Welcome to %s! You have been checked in to room %s.",
                booking.getRoom().getHotel().getName(),
                booking.getRoom().getRoomNumber()
        );
        sendNotification(
                booking.getUser(),
                NotificationType.BOOKING_CHECKED_IN,
                message,
                booking.getId(),
                "BOOKING"
        );
    }

    @Override
    public void notifyBookingCheckedOut(Booking booking) {
        String message = String.format(
                "Thank you for staying at %s! We hope you enjoyed your visit.",
                booking.getRoom().getHotel().getName()
        );
        sendNotification(
                booking.getUser(),
                NotificationType.BOOKING_CHECKED_OUT,
                message,
                booking.getId(),
                "BOOKING"
        );
    }

    // ==========================================================
    // Auto-generation methods for Payment events
    // ==========================================================

    @Override
    public void notifyPaymentReceived(Payment payment) {
        String message = String.format(
                "Payment of $%.2f has been received for your booking at %s.",
                payment.getAmount(),
                payment.getBooking().getRoom().getHotel().getName()
        );
        sendNotification(
                payment.getBooking().getUser(),
                NotificationType.PAYMENT_RECEIVED,
                message,
                payment.getId(),
                "PAYMENT"
        );
    }

    @Override
    public void notifyPaymentFailed(Payment payment) {
        String message = String.format(
                "Payment of $%.2f for your booking at %s could not be processed. Please try again.",
                payment.getAmount(),
                payment.getBooking().getRoom().getHotel().getName()
        );
        sendNotification(
                payment.getBooking().getUser(),
                NotificationType.PAYMENT_FAILED,
                message,
                payment.getId(),
                "PAYMENT"
        );
    }

    @Override
    public void notifyPaymentRefunded(Payment payment) {
        String message = String.format(
                "A refund of $%.2f has been processed for your booking at %s.",
                payment.getRefundAmount(),
                payment.getBooking().getRoom().getHotel().getName()
        );
        sendNotification(
                payment.getBooking().getUser(),
                NotificationType.PAYMENT_REFUNDED,
                message,
                payment.getId(),
                "PAYMENT"
        );
    }

    // ==========================================================
    // Auto-generation methods for Review events
    // ==========================================================

    @Override
    public void notifyReviewApproved(Review review) {
        String message = String.format(
                "Your review for %s has been approved and is now visible to other guests.",
                review.getHotel().getName()
        );
        sendNotification(
                review.getUser(),
                NotificationType.REVIEW_APPROVED,
                message,
                review.getId(),
                "REVIEW"
        );
    }

    @Override
    public void notifyReviewResponded(Review review) {
        String message = String.format(
                "%s has responded to your review.",
                review.getHotel().getName()
        );
        sendNotification(
                review.getUser(),
                NotificationType.REVIEW_RESPONDED,
                message,
                review.getId(),
                "REVIEW"
        );
    }

    // ==========================================================
    // Generic notification methods
    // ==========================================================

    @Override
    public void sendNotification(User user, NotificationType type, String message) {
        sendNotification(user, type, message, null, null);
    }

    @Override
    public void sendNotification(User user, NotificationType type, String message,
                                  Long referenceId, String referenceType) {
        Notification notification = new Notification(user, type, message);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);
        notificationRepository.save(notification);
    }
}
