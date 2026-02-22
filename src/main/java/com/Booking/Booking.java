package com.Booking;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // أي حقول ثانية عندك
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}