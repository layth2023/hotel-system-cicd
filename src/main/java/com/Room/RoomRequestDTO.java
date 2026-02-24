package com.Room;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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




}
