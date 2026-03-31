package com.Payment;

/**
 * Exception thrown when a payment is not found.
 */
public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(Long id) {
        super("Payment not found with id: " + id);
    }

    public PaymentNotFoundException(String transactionId) {
        super("Payment not found with transaction id: " + transactionId);
    }

    public PaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
