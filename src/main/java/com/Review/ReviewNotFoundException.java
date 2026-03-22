package com.Review;

/**
 * Exception thrown when a review is not found.
 */
public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(Long id) {
        super("Review not found with id: " + id);
    }

    public ReviewNotFoundException(String message) {
        super(message);
    }
}
