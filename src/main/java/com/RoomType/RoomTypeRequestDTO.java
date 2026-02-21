package com.RoomType;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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





}
