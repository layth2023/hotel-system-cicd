package com.Notification;

import com.Booking.Booking;
import com.Payment.Payment;
import com.Review.Review;
import com.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    // User-facing methods
    Page<NotificationResponseDTO> getUserNotifications(Long userId, Pageable pageable);

    Page<NotificationResponseDTO> getUnreadNotifications(Long userId, Pageable pageable);

    NotificationResponseDTO getById(Long id, Long userId);

    NotificationResponseDTO markAsRead(Long id, Long userId);

    int markAllAsRead(Long userId);

    void delete(Long id, Long userId);

    long getUnreadCount(Long userId);

    // Auto-generation methods (called by other services)
    void notifyBookingCreated(Booking booking);

    void notifyBookingConfirmed(Booking booking);

    void notifyBookingCancelled(Booking booking);

    void notifyBookingCheckedIn(Booking booking);

    void notifyBookingCheckedOut(Booking booking);

    void notifyPaymentReceived(Payment payment);

    void notifyPaymentFailed(Payment payment);

    void notifyPaymentRefunded(Payment payment);

    void notifyReviewApproved(Review review);

    void notifyReviewResponded(Review review);

    // Generic notification
    void sendNotification(User user, NotificationType type, String message);

    void sendNotification(User user, NotificationType type, String message, Long referenceId, String referenceType);
}
