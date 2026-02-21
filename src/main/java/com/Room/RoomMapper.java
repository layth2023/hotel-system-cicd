package com.Room;

import com.RoomType.RoomType;

public class RoomMapper {
    private RoomMapper() {}

    public static Room toEntity(RoomRequestDTO dto, RoomType roomType) {
        Room e = new Room();
        e.setRoomNumber(dto.getRoomNumber());
        e.setFloor(dto.getFloor());
        e.setRoomType(roomType);

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
