package com.Payment;

/**
 * Exception thrown for invalid payment operations.
 */
public class PaymentBadRequestException extends RuntimeException {

    public PaymentBadRequestException(String message) {
        super(message);
    }

    public PaymentBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
