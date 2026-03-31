package com.BookingGuest;

import java.time.LocalDateTime;

public class BookingGuestResponseDTO {

    private Long id;
    private Long bookingId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private boolean primaryGuest;
    private String documentType;
    private String documentNumber;
    private String nationality;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookingGuestResponseDTO() {}

    public BookingGuestResponseDTO(Long id, Long bookingId, String firstName, String lastName,
                                   String email, String phone, boolean primaryGuest,
                                   String documentType, String documentNumber, String nationality,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.email = email;
        this.phone = phone;
        this.primaryGuest = primaryGuest;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.nationality = nationality;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isPrimaryGuest() {
        return primaryGuest;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getNationality() {
        return nationality;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
