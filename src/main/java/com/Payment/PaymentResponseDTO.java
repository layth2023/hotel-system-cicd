package com.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponseDTO {

    private Long id;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private Long bookingId;

    public PaymentResponseDTO(Long id, BigDecimal amount,
                              PaymentStatus status,
                              LocalDateTime createdAt,
                              Long bookingId) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.bookingId = bookingId;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getBookingId() {
        return bookingId;
    }
}