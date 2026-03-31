package com.RoomType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for RoomType responses.
 */
public class RoomTypeResponseDTO {

    private Long id;
    private String name;
    private BigDecimal pricePerNight;
    private BigDecimal seasonalPrice;
    private Integer freeCancellationHours;
    private String cancellationRules;
    private Integer capacity;
    private String imagePath;
    private String description;
    private Integer beds;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public RoomTypeResponseDTO() {}

    public RoomTypeResponseDTO(Long id, String name, BigDecimal pricePerNight,
                               BigDecimal seasonalPrice, Integer capacity,
                               Integer freeCancellationHours, String cancellationRules,
                               String imagePath, String description, Integer beds) {
        this.id = id;
        this.name = name;
        this.pricePerNight = pricePerNight;
        this.seasonalPrice = seasonalPrice;
        this.capacity = capacity;
        this.freeCancellationHours = freeCancellationHours;
        this.cancellationRules = cancellationRules;
        this.imagePath = imagePath;
        this.description = description;
        this.beds = beds;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getBeds() {
        return beds;
    }

    public void setBeds(Integer beds) {
        this.beds = beds;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
