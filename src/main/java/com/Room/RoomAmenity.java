package com.Room;

import com.Amenity.Amenity;
import com.Common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Pivot entity representing amenities assigned to a room with quantity and pricing.
 * Allows tracking how many of each amenity exists in a room and its price impact.
 */
@Entity
@Table(name = "room_amenity_details", indexes = {
        @Index(name = "idx_room_amenity_room", columnList = "room_id"),
        @Index(name = "idx_room_amenity_amenity", columnList = "amenity_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_amenity", columnNames = {"room_id", "amenity_id"})
})
public class RoomAmenity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Room is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore
    private Room room;

    @NotNull(message = "Amenity is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amenity_id", nullable = false)
    private Amenity amenity;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity = 1;

    @DecimalMin(value = "0.00", message = "Price per unit cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    @Column(name = "price_per_unit", precision = 10, scale = 2)
    private BigDecimal pricePerUnit = BigDecimal.ZERO;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Column(length = 500)
    private String notes;

    // Constructors
    public RoomAmenity() {}

    public RoomAmenity(Room room, Amenity amenity, Integer quantity) {
        this.room = room;
        this.amenity = amenity;
        this.quantity = quantity;
    }

    public RoomAmenity(Room room, Amenity amenity, Integer quantity, BigDecimal pricePerUnit) {
        this.room = room;
        this.amenity = amenity;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
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

    public Amenity getAmenity() {
        return amenity;
    }

    public void setAmenity(Amenity amenity) {
        this.amenity = amenity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Calculate total price for this amenity (quantity * pricePerUnit)
     */
    public BigDecimal getTotalPrice() {
        if (pricePerUnit == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return pricePerUnit.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomAmenity that = (RoomAmenity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RoomAmenity{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", pricePerUnit=" + pricePerUnit +
                '}';
    }
}
