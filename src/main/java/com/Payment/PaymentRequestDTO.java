package com.Payment;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PaymentRequestDTO {

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Long bookingId;

    // getters & setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
}