package com.Amenity;

import com.Common.BaseEntity;
import com.Hotel.Hotel;
import com.Room.Room;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Amenity entity representing hotel and room amenities.
 * Can be associated with both hotels and individual rooms.
 */
@Entity
@Table(name = "amenities", indexes = {
        @Index(name = "idx_amenity_name", columnList = "name")
})
public class Amenity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Amenity name is required")
    @Size(min = 2, max = 80, message = "Amenity name must be between 2 and 80 characters")
    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Size(max = 100, message = "Icon name must not exceed 100 characters")
    @Column(length = 100)
    private String icon;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    @Column(length = 50)
    private String category;

    @JsonIgnore
    @ManyToMany(mappedBy = "amenities")
    private Set<Hotel> hotels = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "amenities")
    private Set<Room> rooms = new HashSet<>();

    // Constructors
    public Amenity() {}

    public Amenity(String name) {
        this.name = name;
    }

    public Amenity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Amenity(Long id, String name, String description, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<Hotel> getHotels() {
        return hotels;
    }

    public void setHotels(Set<Hotel> hotels) {
        this.hotels = hotels;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amenity amenity = (Amenity) o;
        return Objects.equals(id, amenity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Amenity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
