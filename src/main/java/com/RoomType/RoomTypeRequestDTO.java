package com.RoomType;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


public class RoomTypeRequestDTO {
    @NotBlank(message = "Room type name is required")
    private String name;
    @NotNull
    @Positive
    private Double pricePerNight;
    @Positive
    private Double seasonalPrice;
    @PositiveOrZero
    private Double freeCancellationHours;
    @Size(max = 500)
    private String cancellationRules;
    @NotNull
    @Min(1)
    private Integer capacity;
    @Size(max = 1000)
    private String description;
    private String imagePath;
    @NotNull
    @Min(1)
    private Integer beds;

    public @NotBlank(message = "Room type name is required") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Room type name is required") String name) {
        this.name = name;
    }

    public @NotNull @Positive Double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(@NotNull @Positive Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public @Positive Double getSeasonalPrice() {
        return seasonalPrice;
    }

    public void setSeasonalPrice(@Positive Double seasonalPrice) {
        this.seasonalPrice = seasonalPrice;
    }

    public @PositiveOrZero Double getFreeCancellationHours() {
        return freeCancellationHours;
    }

    public void setFreeCancellationHours(@PositiveOrZero Double freeCancellationHours) {
        this.freeCancellationHours = freeCancellationHours;
    }

    public @Size(max = 500) String getCancellationRules() {
        return cancellationRules;
    }

    public void setCancellationRules(@Size(max = 500) String cancellationRules) {
        this.cancellationRules = cancellationRules;
    }

    public @NotNull @Min(1) Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(@NotNull @Min(1) Integer capacity) {
        this.capacity = capacity;
    }

    public @Size(max = 1000) String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 1000) String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public @NotNull @Min(1) Integer getBeds() {
        return beds;
    }

    public void setBeds(@NotNull @Min(1) Integer beds) {
        this.beds = beds;
    }
}
