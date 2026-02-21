package com.Room;

import com.RoomType.RoomType;
import com.RoomType.RoomTypeNotFoundException;
import com.RoomType.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomServiceImpl implements RoomServiceInt {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomServiceImpl(RoomRepository roomRepository, RoomTypeRepository roomTypeRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    @Transactional(readOnly = true)
    public List<RoomResponseDTO> findAll() {
        return roomRepository.findAll()
                .stream()
                .map(RoomMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomResponseDTO findById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
        return RoomMapper.toDto(room);
    }

    public RoomResponseDTO create(RoomRequestDTO dto) {

        if (roomRepository.existsByRoomNumber(dto.getRoomNumber())) {
            throw new RoomAlreadyExistsException(dto.getRoomNumber());
        }

        RoomType roomType = roomTypeRepository.findById(dto.getRoomTypeId())
                .orElseThrow(() -> new RoomTypeNotFoundException(dto.getRoomTypeId()));

        Room room = RoomMapper.toEntity(dto, roomType);
        Room saved = roomRepository.save(room);

        return RoomMapper.toDto(saved);
    }

    public RoomResponseDTO update(Long id, RoomRequestDTO dto) {
        Room existing = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));

        RoomType roomType = roomTypeRepository.findById(dto.getRoomTypeId())
                .orElseThrow(() -> new RoomTypeNotFoundException(dto.getRoomTypeId()));

        existing.setRoomNumber(dto.getRoomNumber());
        existing.setFloor(dto.getFloor());
        existing.setRoomType(roomType);

        Room updated = roomRepository.save(existing);
        return RoomMapper.toDto(updated);
    }

    public void deleteById(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException(id);
        }
        roomRepository.deleteById(id);
    }


    @Override
    public RoomResponseDTO assignRoomType(Long roomId, Long roomTypeId) {
        // Fetch room
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        // Fetch room type
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RoomTypeNotFoundException(roomTypeId));

        // Assign
        room.setRoomType(roomType);

        Room updated = roomRepository.save(room);

        return RoomMapper.toDto(updated);
    }

}
