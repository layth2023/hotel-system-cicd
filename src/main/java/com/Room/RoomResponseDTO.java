package com.Room;

import lombok.Getter;

@Getter
public class RoomResponseDTO {
    private final Long id;
    private String roomNumber;
    private Integer floor;
    private Long roomTypeId;
    private String roomTypeName;

    public RoomResponseDTO(Long id, String roomNumber, Integer floor, Long roomTypeId, String roomTypeName) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;

    }

}
