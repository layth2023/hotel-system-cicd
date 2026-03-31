package com.BookingGuest;

import com.Booking.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingGuestMapper {

    public BookingGuest toEntity(BookingGuestRequestDTO dto, Booking booking) {
        BookingGuest entity = new BookingGuest();
        entity.setBooking(booking);
        entity.setFirstName(dto.getFirstName() != null ? dto.getFirstName().trim() : null);
        entity.setLastName(dto.getLastName() != null ? dto.getLastName().trim() : null);
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setPrimaryGuest(dto.isPrimaryGuest());
        entity.setDocumentType(dto.getDocumentType());
        entity.setDocumentNumber(dto.getDocumentNumber());
        entity.setNationality(dto.getNationality());
        return entity;
    }

    public void updateEntity(BookingGuest entity, BookingGuestRequestDTO dto) {
        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName().trim());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName().trim());
        }
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setPrimaryGuest(dto.isPrimaryGuest());
        entity.setDocumentType(dto.getDocumentType());
        entity.setDocumentNumber(dto.getDocumentNumber());
        entity.setNationality(dto.getNationality());
    }

    public BookingGuestResponseDTO toResponseDTO(BookingGuest entity) {
        return new BookingGuestResponseDTO(
                entity.getId(),
                entity.getBooking().getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.isPrimaryGuest(),
                entity.getDocumentType(),
                entity.getDocumentNumber(),
                entity.getNationality(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
