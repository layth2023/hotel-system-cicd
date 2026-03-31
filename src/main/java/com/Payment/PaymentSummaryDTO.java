package com.Payment;

import java.math.BigDecimal;

public class PaymentSummaryDTO {

    private Long bookingId;
    private BigDecimal totalAmount;
    private BigDecimal totalPaid;
    private BigDecimal totalRefunded;
    private BigDecimal balanceDue;
    private int paymentCount;
    private boolean fullyPaid;

    public PaymentSummaryDTO() {}

    public PaymentSummaryDTO(Long bookingId, BigDecimal totalAmount, BigDecimal totalPaid,
                             BigDecimal totalRefunded, BigDecimal balanceDue, int paymentCount, boolean fullyPaid) {
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
        this.totalPaid = totalPaid;
        this.totalRefunded = totalRefunded;
        this.balanceDue = balanceDue;
        this.paymentCount = paymentCount;
        this.fullyPaid = fullyPaid;
    }

    // Getters
    public Long getBookingId() { return bookingId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getTotalPaid() { return totalPaid; }
    public BigDecimal getTotalRefunded() { return totalRefunded; }
    public BigDecimal getBalanceDue() { return balanceDue; }
    public int getPaymentCount() { return paymentCount; }
    public boolean isFullyPaid() { return fullyPaid; }

    // Setters
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
    public void setTotalRefunded(BigDecimal totalRefunded) { this.totalRefunded = totalRefunded; }
    public void setBalanceDue(BigDecimal balanceDue) { this.balanceDue = balanceDue; }
    public void setPaymentCount(int paymentCount) { this.paymentCount = paymentCount; }
    public void setFullyPaid(boolean fullyPaid) { this.fullyPaid = fullyPaid; }
}
