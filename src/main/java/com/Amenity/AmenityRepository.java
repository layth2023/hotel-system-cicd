package com.Amenity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    Page<Amenity> findAllByIsActiveTrue(Pageable pageable);

    Page<Amenity> findAllByIsActiveFalse(Pageable pageable);

    Page<Amenity> findByHotels_IdAndIsActiveFalse(Long hotelId, Pageable pageable);

    // Hotel -> Amenities (all, admin use case)
    Page<Amenity> findByHotels_Id(Long hotelId, Pageable pageable);

    // Hotel -> Amenities (active only, user use case)
    Page<Amenity> findByHotels_IdAndIsActiveTrue(Long hotelId, Pageable pageable);

    @Query("""
           SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
           FROM Amenity a
           WHERE LOWER(a.name) = LOWER(:name)
           """)
    boolean existsByNameInsensitive(@Param("name") String name);

    @Query("""
           SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
           FROM Amenity a
           WHERE LOWER(a.name) = LOWER(:name)
           AND a.id <> :id
           """)
    boolean existsByNameInsensitiveAndIdNot(@Param("name") String name,
                                            @Param("id") Long id);
}