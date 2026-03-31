package com.Room;

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

    public Long getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }
}
