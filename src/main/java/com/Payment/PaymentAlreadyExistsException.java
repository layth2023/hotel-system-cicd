package com.Payment;

/**
 * Exception thrown when a payment already exists.
 */
public class PaymentAlreadyExistsException extends RuntimeException {

    public PaymentAlreadyExistsException(String message) {
        super(message);
    }

    public PaymentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
