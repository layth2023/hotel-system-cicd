package com.Room;

import java.util.List;

public interface RoomServiceInt {

    RoomResponseDTO create(RoomRequestDTO dto);

    RoomResponseDTO findById(Long id);

    List<RoomResponseDTO> findAll();

    RoomResponseDTO update(Long id, RoomRequestDTO dto);

    void deleteById(Long id);

    RoomResponseDTO assignRoomType(Long roomId, Long roomTypeId);

}
