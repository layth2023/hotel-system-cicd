package com.RoomAvailability;

import com.Room.Room;
import com.Room.RoomNotFoundException;
import com.Room.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RoomAvailabilityServiceImpl implements RoomAvailabilityService {

    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final RoomAvailabilityMapper roomAvailabilityMapper;
    private final RoomRepository roomRepository;

    public RoomAvailabilityServiceImpl(RoomAvailabilityRepository roomAvailabilityRepository,
                                       RoomAvailabilityMapper roomAvailabilityMapper,
                                       RoomRepository roomRepository) {
        this.roomAvailabilityRepository = roomAvailabilityRepository;
        this.roomAvailabilityMapper = roomAvailabilityMapper;
        this.roomRepository = roomRepository;
    }

    @Override
    public RoomAvailabilityResponseDTO create(RoomAvailabilityRequestDTO requestDTO) {
        Room room = roomRepository.findById(requestDTO.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(requestDTO.getRoomId()));

        // Check if availability already exists for this room and date
        roomAvailabilityRepository.findByRoomIdAndDate(requestDTO.getRoomId(), requestDTO.getDate())
                .ifPresent(existing -> {
                    throw new RoomAvailabilityAlreadyExistsException(requestDTO.getRoomId(), requestDTO.getDate());
                });

        RoomAvailability entity = roomAvailabilityMapper.toEntity(requestDTO, room);
        RoomAvailability saved = roomAvailabilityRepository.save(entity);

        return roomAvailabilityMapper.toResponseDTO(saved);
    }

    @Override
    public RoomAvailabilityResponseDTO getById(Long id) {
        RoomAvailability entity = roomAvailabilityRepository.findById(id)
                .orElseThrow(() -> new RoomAvailabilityNotFoundException(id));
        return roomAvailabilityMapper.toResponseDTO(entity);
    }

    @Override
    public RoomAvailabilityResponseDTO update(Long id, RoomAvailabilityRequestDTO requestDTO) {
        RoomAvailability entity = roomAvailabilityRepository.findById(id)
                .orElseThrow(() -> new RoomAvailabilityNotFoundException(id));

        roomAvailabilityMapper.updateEntity(entity, requestDTO);
        RoomAvailability saved = roomAvailabilityRepository.save(entity);

        return roomAvailabilityMapper.toResponseDTO(saved);
    }

    @Override
    public void delete(Long id) {
        RoomAvailability entity = roomAvailabilityRepository.findById(id)
                .orElseThrow(() -> new RoomAvailabilityNotFoundException(id));
        roomAvailabilityRepository.delete(entity);
    }

    @Override
    public Page<RoomAvailabilityResponseDTO> getByRoomId(Long roomId, Pageable pageable) {
        ensureRoomExists(roomId);
        return roomAvailabilityRepository.findByRoomId(roomId, pageable)
                .map(roomAvailabilityMapper::toResponseDTO);
    }

    @Override
    public List<RoomAvailabilityResponseDTO> getByRoomIdAndDateRange(Long roomId, LocalDate startDate, LocalDate endDate) {
        ensureRoomExists(roomId);
        validateDateRange(startDate, endDate);

        return roomAvailabilityRepository.findByRoomIdAndDateBetween(roomId, startDate, endDate)
                .stream()
                .map(roomAvailabilityMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<RoomAvailabilityResponseDTO> bulkCreateOrUpdate(RoomAvailabilityBulkRequestDTO requestDTO) {
        Room room = roomRepository.findById(requestDTO.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(requestDTO.getRoomId()));

        validateDateRange(requestDTO.getStartDate(), requestDTO.getEndDate());

        List<RoomAvailabilityResponseDTO> results = new ArrayList<>();
        LocalDate current = requestDTO.getStartDate();

        while (!current.isAfter(requestDTO.getEndDate())) {
            final LocalDate date = current;

            RoomAvailability entity = roomAvailabilityRepository
                    .findByRoomIdAndDate(requestDTO.getRoomId(), date)
                    .orElseGet(() -> {
                        RoomAvailability newEntity = new RoomAvailability();
                        newEntity.setRoom(room);
                        newEntity.setDate(date);
                        return newEntity;
                    });

            entity.setAvailable(requestDTO.isAvailable());
            if (requestDTO.getPrice() != null) {
                entity.setPrice(requestDTO.getPrice());
            }
            if (requestDTO.getMinStay() != null) {
                entity.setMinStay(requestDTO.getMinStay());
            }
            entity.setMaxStay(requestDTO.getMaxStay());
            entity.setNotes(requestDTO.getNotes() != null ? requestDTO.getNotes().trim() : null);

            RoomAvailability saved = roomAvailabilityRepository.save(entity);
            results.add(roomAvailabilityMapper.toResponseDTO(saved));

            current = current.plusDays(1);
        }

        return results;
    }

    @Override
    public void bulkDelete(Long roomId, LocalDate startDate, LocalDate endDate) {
        ensureRoomExists(roomId);
        validateDateRange(startDate, endDate);
        roomAvailabilityRepository.deleteByRoomIdAndDateBetween(roomId, startDate, endDate);
    }

    private void ensureRoomExists(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
    }
}
