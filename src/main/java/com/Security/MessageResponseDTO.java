package com.Security;

/**
 * Simple message response DTO for success/error messages.
 */
public class MessageResponseDTO {

    private String message;
    private boolean success;

    // Constructors
    public MessageResponseDTO() {}

    public MessageResponseDTO(String message) {
        this.message = message;
        this.success = true;
    }

    public MessageResponseDTO(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    // Static factory methods
    public static MessageResponseDTO success(String message) {
        return new MessageResponseDTO(message, true);
    }

    public static MessageResponseDTO error(String message) {
        return new MessageResponseDTO(message, false);
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
