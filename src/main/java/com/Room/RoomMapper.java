package com.Room;

import com.Hotel.Hotel;
import com.RoomType.RoomType;

public class RoomMapper {
    private RoomMapper() {}

    public static Room toEntity(RoomRequestDTO dto, RoomType roomType, Hotel hotel) {
        Room e = new Room();
        e.setRoomNumber(dto.getRoomNumber());
        e.setFloor(dto.getFloor());
        e.setRoomType(roomType);
        e.setHotel(hotel);

        return e;
    }

    public static RoomResponseDTO toDto(Room e) {
        return new RoomResponseDTO(
                e.getId(),
                e.getRoomNumber(),
                e.getFloor(),
                e.getRoomType().getId(),
                e.getRoomType().getName()

        );
    }
}
