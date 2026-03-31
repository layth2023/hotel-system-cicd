package com.PaymentTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentTransactionResponseDTO {

    private Long id;
    private Long paymentId;
    private String transactionId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String gatewayResponse;
    private String errorMessage;
    private LocalDateTime processedAt;
    private String gatewayName;
    private LocalDateTime createdAt;

    public PaymentTransactionResponseDTO() {}

    public PaymentTransactionResponseDTO(Long id, Long paymentId, String transactionId,
                                         BigDecimal amount, TransactionType type, TransactionStatus status,
                                         String gatewayResponse, String errorMessage, LocalDateTime processedAt,
                                         String gatewayName, LocalDateTime createdAt) {
        this.id = id;
        this.paymentId = paymentId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.gatewayResponse = gatewayResponse;
        this.errorMessage = errorMessage;
        this.processedAt = processedAt;
        this.gatewayName = gatewayName;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public Long getPaymentId() { return paymentId; }
    public String getTransactionId() { return transactionId; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public String getGatewayResponse() { return gatewayResponse; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public String getGatewayName() { return gatewayName; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Static factory method
    public static PaymentTransactionResponseDTO from(PaymentTransaction entity) {
        return new PaymentTransactionResponseDTO(
                entity.getId(),
                entity.getPayment().getId(),
                entity.getTransactionId(),
                entity.getAmount(),
                entity.getType(),
                entity.getStatus(),
                entity.getGatewayResponse(),
                entity.getErrorMessage(),
                entity.getProcessedAt(),
                entity.getGatewayName(),
                entity.getCreatedAt()
        );
    }
}
