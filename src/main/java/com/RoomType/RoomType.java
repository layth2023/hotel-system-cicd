package com.RoomType;

import com.Common.BaseEntity;
import com.Room.Room;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RoomType entity representing different types of rooms available.
 * Contains pricing, capacity, and room characteristics.
 */
@Entity
@Table(name = "room_types", indexes = {
        @Index(name = "idx_room_type_name", columnList = "name")
})
public class RoomType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room type name is required")
    @Size(min = 2, max = 100, message = "Room type name must be between 2 and 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 20, message = "Capacity must not exceed 20")
    @Column(nullable = false)
    private Integer capacity;

    @NotNull(message = "Number of beds is required")
    @Min(value = 1, message = "Number of beds must be at least 1")
    @Max(value = 10, message = "Number of beds must not exceed 10")
    @Column(nullable = false)
    private Integer beds;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @DecimalMin(value = "0.00", message = "Seasonal price cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Seasonal price format is invalid")
    @Column(name = "seasonal_price", precision = 10, scale = 2)
    private BigDecimal seasonalPrice;

    @PositiveOrZero(message = "Free cancellation hours cannot be negative")
    @Column(name = "free_cancellation_hours")
    private Integer freeCancellationHours;

    @NotBlank(message = "Cancellation rules are required")
    @Size(max = 500, message = "Cancellation rules must not exceed 500 characters")
    @Column(name = "cancellation_rules", nullable = false, length = 500)
    private String cancellationRules;

    @Size(max = 500, message = "Image path must not exceed 500 characters")
    @Column(name = "image_path", length = 500)
    private String imagePath;

    @Column(nullable = false)
    private boolean active = true;

    @JsonIgnore
    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();

    // Constructors
    public RoomType() {}

    public RoomType(String name, Integer capacity, Integer beds, BigDecimal pricePerNight, String cancellationRules) {
        this.name = name;
        this.capacity = capacity;
        this.beds = beds;
        this.pricePerNight = pricePerNight;
        this.cancellationRules = cancellationRules;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getBeds() {
        return beds;
    }

    public void setBeds(Integer beds) {
        this.beds = beds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public BigDecimal getSeasonalPrice() {
        return seasonalPrice;
    }

    public void setSeasonalPrice(BigDecimal seasonalPrice) {
        this.seasonalPrice = seasonalPrice;
    }

    public Integer getFreeCancellationHours() {
        return freeCancellationHours;
    }

    public void setFreeCancellationHours(Integer freeCancellationHours) {
        this.freeCancellationHours = freeCancellationHours;
    }

    public String getCancellationRules() {
        return cancellationRules;
    }

    public void setCancellationRules(String cancellationRules) {
        this.cancellationRules = cancellationRules;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * Get the effective price considering seasonal pricing
     */
    public BigDecimal getEffectivePrice() {
        return seasonalPrice != null ? seasonalPrice : pricePerNight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomType roomType)) return false;
        return Objects.equals(id, roomType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RoomType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", beds=" + beds +
                ", pricePerNight=" + pricePerNight +
                '}';
    }
}
