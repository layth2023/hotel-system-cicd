package com.PaymentTransaction;

import com.Common.BaseEntity;
import com.Payment.Payment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * PaymentTransaction entity for tracking individual payment gateway transactions.
 */
@Entity
@Table(name = "payment_transactions", indexes = {
        @Index(name = "idx_transaction_payment", columnList = "payment_id"),
        @Index(name = "idx_transaction_id", columnList = "transaction_id")
})
public class PaymentTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Payment is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    @JsonIgnore
    private Payment payment;

    @Size(max = 100, message = "Transaction ID must not exceed 100 characters")
    @Column(name = "transaction_id", unique = true, length = 100)
    private String transactionId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount format is invalid")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @NotNull(message = "Transaction status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "gateway_response", length = 2000)
    private String gatewayResponse;

    @Size(max = 500, message = "Error message must not exceed 500 characters")
    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Size(max = 50, message = "Gateway name must not exceed 50 characters")
    @Column(name = "gateway_name", length = 50)
    private String gatewayName;

    // Constructors
    public PaymentTransaction() {}

    public PaymentTransaction(Payment payment, BigDecimal amount, TransactionType type) {
        this.payment = payment;
        this.amount = amount;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getGatewayResponse() {
        return gatewayResponse;
    }

    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    // Utility methods
    public void markAsSuccess(String transactionId, String gatewayResponse) {
        this.status = TransactionStatus.SUCCESS;
        this.transactionId = transactionId;
        this.gatewayResponse = gatewayResponse;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = TransactionStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentTransaction that = (PaymentTransaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PaymentTransaction{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}
