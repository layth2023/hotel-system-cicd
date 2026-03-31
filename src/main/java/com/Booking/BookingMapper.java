package com.Booking;

import com.Room.Room;
import com.User.User;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequestDTO dto, User user, Room room) {
        Booking entity = new Booking();
        entity.setUser(user);
        entity.setRoom(room);
        entity.setCheckInDate(dto.getCheckInDate());
        entity.setCheckOutDate(dto.getCheckOutDate());
        entity.setNumberOfGuests(dto.getNumberOfGuests());
        entity.setNumberOfAdults(dto.getNumberOfAdults());
        entity.setNumberOfChildren(dto.getNumberOfChildren() != null ? dto.getNumberOfChildren() : 0);
        entity.setSpecialRequests(dto.getSpecialRequests());
        entity.setNotes(dto.getNotes());
        entity.setStatus(BookingStatus.PENDING);

        // Set price from room type
        entity.setPricePerNight(room.getRoomType().getPricePerNight());
        entity.setTotalPrice(entity.calculateTotalPrice());

        return entity;
    }

    public void updateEntity(Booking entity, BookingRequestDTO dto) {
        if (dto.getCheckInDate() != null) {
            entity.setCheckInDate(dto.getCheckInDate());
        }
        if (dto.getCheckOutDate() != null) {
            entity.setCheckOutDate(dto.getCheckOutDate());
        }
        if (dto.getNumberOfGuests() != null) {
            entity.setNumberOfGuests(dto.getNumberOfGuests());
        }
        if (dto.getNumberOfAdults() != null) {
            entity.setNumberOfAdults(dto.getNumberOfAdults());
        }
        if (dto.getNumberOfChildren() != null) {
            entity.setNumberOfChildren(dto.getNumberOfChildren());
        }
        entity.setSpecialRequests(dto.getSpecialRequests());
        entity.setNotes(dto.getNotes());

        // Recalculate total price
        entity.setTotalPrice(entity.calculateTotalPrice());
    }

    public BookingResponseDTO toResponseDTO(Booking entity) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(entity.getId());
        dto.setConfirmationNumber(entity.getConfirmationNumber());
        dto.setUserId(entity.getUser().getId());
        dto.setUsername(entity.getUser().getUsername());
        dto.setRoomId(entity.getRoom().getId());
        dto.setRoomNumber(entity.getRoom().getRoomNumber());
        dto.setHotelId(entity.getRoom().getHotel().getId());
        dto.setHotelName(entity.getRoom().getHotel().getName());
        dto.setRoomTypeName(entity.getRoom().getRoomType().getName());
        dto.setCheckInDate(entity.getCheckInDate());
        dto.setCheckOutDate(entity.getCheckOutDate());
        dto.setNumberOfGuests(entity.getNumberOfGuests());
        dto.setNumberOfAdults(entity.getNumberOfAdults());
        dto.setNumberOfChildren(entity.getNumberOfChildren());
        dto.setPricePerNight(entity.getPricePerNight());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setTaxAmount(entity.getTaxAmount());
        dto.setDiscount(entity.getDiscount());
        dto.setAmenityTotal(entity.getAmenityTotal());
        dto.setTotalPaid(entity.getTotalPaid());
        dto.setBalanceDue(entity.getBalanceDue());
        dto.setStatus(entity.getStatus());
        dto.setSpecialRequests(entity.getSpecialRequests());
        dto.setNotes(entity.getNotes());
        dto.setActualCheckIn(entity.getActualCheckIn());
        dto.setActualCheckOut(entity.getActualCheckOut());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
