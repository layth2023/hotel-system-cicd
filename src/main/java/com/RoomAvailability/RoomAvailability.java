package com.RoomAvailability;

import com.Common.BaseEntity;
import com.Room.Room;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * RoomAvailability entity for managing room availability and dynamic pricing.
 */
@Entity
@Table(name = "room_availability", indexes = {
        @Index(name = "idx_availability_room_date", columnList = "room_id, date")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_date", columnNames = {"room_id", "date"})
})
public class RoomAvailability extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Room is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore
    private Room room;

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Min(value = 1, message = "Minimum stay must be at least 1")
    @Column(name = "min_stay")
    private Integer minStay = 1;

    @Max(value = 365, message = "Maximum stay must not exceed 365")
    @Column(name = "max_stay")
    private Integer maxStay;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Column(length = 500)
    private String notes;

    // Constructors
    public RoomAvailability() {}

    public RoomAvailability(Room room, LocalDate date) {
        this.room = room;
        this.date = date;
    }

    public RoomAvailability(Room room, LocalDate date, boolean available, BigDecimal price) {
        this.room = room;
        this.date = date;
        this.available = available;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getMinStay() {
        return minStay;
    }

    public void setMinStay(Integer minStay) {
        this.minStay = minStay;
    }

    public Integer getMaxStay() {
        return maxStay;
    }

    public void setMaxStay(Integer maxStay) {
        this.maxStay = maxStay;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomAvailability that = (RoomAvailability) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RoomAvailability{" +
                "id=" + id +
                ", date=" + date +
                ", available=" + available +
                ", price=" + price +
                '}';
    }
}
