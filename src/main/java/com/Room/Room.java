package com.Room;

import com.RoomType.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import com.Hotel.Hotel;
import com.Amenity.Amenity;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
public class Room {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomNumber;

    @ManyToOne
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Column(nullable = false)
    @PositiveOrZero
    private Integer floor;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToMany
    @JoinTable(
            name = "room_amenities",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();


    public Room(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id != null && id.equals(room.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomNumber='" + roomNumber + '\'' +
                ", roomType=" + roomType +
                ", floor=" + floor +
                '}';
    }

}
