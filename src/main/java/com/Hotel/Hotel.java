package com.Hotel;

import com.Amenity.Amenity;
import com.Room.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
public class Hotel {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String country;

    @Column(length = 20)
    private String phone;

    @Column(length = 150)
    private String email;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private Set<Room> rooms = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "hotel_amenities",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();

    public Hotel() {
    }

    public Hotel(Long id, String name, String address, String city, String country, String phone, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.country = country;
        this.phone = phone;
        this.email = email;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return id != null && id.equals(hotel.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}