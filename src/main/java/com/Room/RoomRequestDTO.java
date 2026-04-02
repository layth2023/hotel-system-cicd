package com.Room;

import jakarta.validation.constraints.*;

public class RoomRequestDTO {
    @NotBlank(message = "Room number cannot be empty")
    @Size(min = 1, max = 100, message = "Room number must be between 1 and 100")
    private String roomNumber;

    @NotNull(message = "Floor cannot be null")
    @PositiveOrZero
    private Integer floor;

    @NotNull(message = "Room type ID cannot be null")
    @PositiveOrZero
    private Long roomTypeId;

    @NotNull(message = "Hotel ID cannot be null")
    @PositiveOrZero
    private Long hotelId;

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }
}
