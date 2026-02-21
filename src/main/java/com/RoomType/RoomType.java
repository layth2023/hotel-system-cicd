package com.RoomType;

import com.Room.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
public class RoomType {
    // FIELDS OF THE CLASS--------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer beds;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double pricePerNight;

    // example: +20% in summer season → *1.2
    private Double seasonalPrice;

    // free cancellation until X hours before check-in
    private Double freeCancellationHours;

    @Column(nullable = false, length = 500)
    private String cancellationRules;

    private String imagePath;

    @OneToMany
    private List<Room> rooms;

    // CONSTRUCTORS --------------------------
    public RoomType() {}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomType roomType)) return false;
        return id != null && id.equals(roomType.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RoomType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + pricePerNight +
                ", beds=" + beds +
                '}';
    }
}
