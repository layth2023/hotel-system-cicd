package com.Room;

import com.Hotel.Hotel;
import com.Hotel.HotelNotFoundException;
import com.Hotel.HotelRepository;
import com.RoomType.RoomType;
import com.RoomType.RoomTypeNotFoundException;
import com.RoomType.RoomTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomServiceImpl implements RoomServiceInt {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;

    public RoomServiceImpl(RoomRepository roomRepository,
                           RoomTypeRepository roomTypeRepository,
                           HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.hotelRepository = hotelRepository;
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

        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new HotelNotFoundException(dto.getHotelId()));

        Room room = RoomMapper.toEntity(dto, roomType, hotel);
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

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> findByHotelId(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }
        return roomRepository.findByHotelId(hotelId)
                .stream()
                .map(RoomMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponseDTO> findByHotelId(Long hotelId, Pageable pageable) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }
        return roomRepository.findByHotelId(hotelId, pageable)
                .map(RoomMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer guests) {
        validateDateRange(checkInDate, checkOutDate);
        int guestCount = guests != null ? guests : 1;

        return roomRepository.findAvailableRooms(checkInDate, checkOutDate, guestCount)
                .stream()
                .map(RoomMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> findAvailableRoomsByHotel(Long hotelId, LocalDate checkInDate, LocalDate checkOutDate, Integer guests) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }
        validateDateRange(checkInDate, checkOutDate);
        int guestCount = guests != null ? guests : 1;

        return roomRepository.findAvailableRoomsByHotelAndDates(hotelId, checkInDate, checkOutDate, guestCount)
                .stream()
                .map(RoomMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> searchRooms(RoomSearchRequestDTO searchRequest) {
        LocalDate checkIn = searchRequest.getCheckInDate() != null ? searchRequest.getCheckInDate() : LocalDate.now();
        LocalDate checkOut = searchRequest.getCheckOutDate() != null ? searchRequest.getCheckOutDate() : checkIn.plusDays(1);
        int guests = searchRequest.getGuests() != null ? searchRequest.getGuests() : 1;

        validateDateRange(checkIn, checkOut);

        List<Room> rooms;
        if (searchRequest.getHotelId() != null) {
            rooms = roomRepository.findAvailableRoomsByHotelAndDates(
                    searchRequest.getHotelId(), checkIn, checkOut, guests);
        } else {
            rooms = roomRepository.findAvailableRooms(checkIn, checkOut, guests);
        }

        return rooms.stream()
                .filter(room -> {
                    // Filter by price range
                    if (searchRequest.getMinPrice() != null &&
                            room.getRoomType().getPricePerNight().compareTo(searchRequest.getMinPrice()) < 0) {
                        return false;
                    }
                    if (searchRequest.getMaxPrice() != null &&
                            room.getRoomType().getPricePerNight().compareTo(searchRequest.getMaxPrice()) > 0) {
                        return false;
                    }
                    // Filter by room type name
                    if (searchRequest.getRoomTypeName() != null &&
                            !room.getRoomType().getName().toLowerCase()
                                    .contains(searchRequest.getRoomTypeName().toLowerCase())) {
                        return false;
                    }
                    return true;
                })
                .map(RoomMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateDateRange(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
            throw new RoomBadRequestException("Check-out date must be after check-in date");
        }
    }
}
