package com.Notification;

/**
 * Types of notifications that can be sent to users.
 */
public enum NotificationType {
    BOOKING_CREATED("Booking Created", "Your booking has been created"),
    BOOKING_CONFIRMED("Booking Confirmed", "Your booking has been confirmed"),
    BOOKING_CANCELLED("Booking Cancelled", "Your booking has been cancelled"),
    BOOKING_CHECKED_IN("Check-In Complete", "You have been checked in"),
    BOOKING_CHECKED_OUT("Check-Out Complete", "You have been checked out"),
    PAYMENT_RECEIVED("Payment Received", "Your payment has been processed"),
    PAYMENT_FAILED("Payment Failed", "Your payment could not be processed"),
    PAYMENT_REFUNDED("Payment Refunded", "Your refund has been processed"),
    CHECK_IN_REMINDER("Check-In Reminder", "Your check-in date is approaching"),
    REVIEW_APPROVED("Review Approved", "Your review has been approved"),
    REVIEW_RESPONDED("Review Response", "The hotel has responded to your review"),
    GENERAL("General", "General notification");

    private final String title;
    private final String defaultMessage;

    NotificationType(String title, String defaultMessage) {
        this.title = title;
        this.defaultMessage = defaultMessage;
    }

    public String getTitle() {
        return title;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
