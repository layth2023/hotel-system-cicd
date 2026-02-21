package com.RoomType;

public class RoomTypeMapper {
    private RoomTypeMapper() {}

    public static RoomType toEntity(RoomTypeRequestDTO dto){
        RoomType e = new RoomType();
        e.setName(dto.getName());
        e.setCapacity(dto.getCapacity());
        e.setBeds(dto.getBeds());
        e.setPricePerNight(dto.getPricePerNight());
        e.setCancellationRules(dto.getCancellationRules());
        e.setDescription(dto.getDescription());
        e.setImagePath(dto.getImagePath());
        e.setSeasonalPrice(dto.getSeasonalPrice());
        e.setFreeCancellationHours(dto.getFreeCancellationHours());
        return e;
    }
    public static RoomTypeResponseDTO toDto(RoomType e){
        return new RoomTypeResponseDTO(
                e.getId(),
                e.getName(),
                e.getPricePerNight(),
                e.getSeasonalPrice(),
                e.getCapacity(),
                e.getFreeCancellationHours(),
                e.getCancellationRules(),
                e.getImagePath(),
                e.getDescription(),
                e.getBeds()
        );
    }

}
