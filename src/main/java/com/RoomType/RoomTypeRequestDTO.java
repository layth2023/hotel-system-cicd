package com.RoomType;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for RoomType creation/update requests.
 */
public class RoomTypeRequestDTO {

    @NotBlank(message = "Room type name is required")
    @Size(min = 2, max = 100, message = "Room type name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    private BigDecimal pricePerNight;

    @DecimalMin(value = "0.00", message = "Seasonal price cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Seasonal price format is invalid")
    private BigDecimal seasonalPrice;

    @PositiveOrZero(message = "Free cancellation hours cannot be negative")
    private Integer freeCancellationHours;

    @NotBlank(message = "Cancellation rules are required")
    @Size(max = 500, message = "Cancellation rules must not exceed 500 characters")
    private String cancellationRules;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 20, message = "Capacity must not exceed 20")
    private Integer capacity;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 500, message = "Image path must not exceed 500 characters")
    private String imagePath;

    @NotNull(message = "Number of beds is required")
    @Min(value = 1, message = "Number of beds must be at least 1")
    @Max(value = 10, message = "Number of beds must not exceed 10")
    private Integer beds;

    // Constructors
    public RoomTypeRequestDTO() {}

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getBeds() {
        return beds;
    }

    public void setBeds(Integer beds) {
        this.beds = beds;
    }
}
