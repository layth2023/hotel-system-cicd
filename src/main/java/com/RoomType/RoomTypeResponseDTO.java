package com.RoomType;

import lombok.Getter;

@Getter
public class RoomTypeResponseDTO {
    private Long id;
    private String name;
    private Double PricePerNight;
    private Double seasonalPrice;
    private Double freeCancellationHours;
    private String cancellationRules;
    private Integer capacity;
    private String imagePath;
    private String description;
    private Integer beds;


    public RoomTypeResponseDTO(Long id, String name, Double pricePerNight,
                               Double seasonalPrice, Integer capacity,
                               Double freeCancellationHours, String cancellationRules,
                               String imagePath, String description, Integer beds) {
        this.id = id;
        this.name = name;
        this.PricePerNight = pricePerNight;
        this.seasonalPrice = seasonalPrice;
        this.capacity = capacity;
        this.freeCancellationHours = freeCancellationHours;
        this.cancellationRules = cancellationRules;
        this.imagePath = imagePath;
        this.description = description;
        this.beds = beds;
    }

}
