package com.RoomAvailability;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RoomAvailabilityResponseDTO {

    private Long id;
    private Long roomId;
    private String roomNumber;
    private LocalDate date;
    private boolean available;
    private BigDecimal price;
    private Integer minStay;
    private Integer maxStay;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RoomAvailabilityResponseDTO() {}

    public RoomAvailabilityResponseDTO(Long id, Long roomId, String roomNumber, LocalDate date,
                                       boolean available, BigDecimal price, Integer minStay,
                                       Integer maxStay, String notes, LocalDateTime createdAt,
                                       LocalDateTime updatedAt) {
        this.id = id;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.date = date;
        this.available = available;
        this.price = price;
        this.minStay = minStay;
        this.maxStay = maxStay;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isAvailable() {
        return available;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getMinStay() {
        return minStay;
    }

    public Integer getMaxStay() {
        return maxStay;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
