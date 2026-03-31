package com.Hotel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("""
           SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END
           FROM Hotel h
           WHERE LOWER(h.name) = LOWER(:name)
           """)
    boolean existsByNameInsensitive(@Param("name") String name);

    @Query("""
           SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END
           FROM Hotel h
           WHERE LOWER(h.name) = LOWER(:name)
           AND h.id <> :id
           """)
    boolean existsByNameInsensitiveAndIdNot(@Param("name") String name, @Param("id") Long id);

    @Query("""
           SELECT h FROM Hotel h
           WHERE (:city IS NULL OR LOWER(h.city) LIKE LOWER(CONCAT('%', :city, '%')))
           AND (:country IS NULL OR LOWER(h.country) LIKE LOWER(CONCAT('%', :country, '%')))
           AND (:minStarRating IS NULL OR h.starRating >= :minStarRating)
           """)
    Page<Hotel> searchHotels(
            @Param("city") String city,
            @Param("country") String country,
            @Param("minStarRating") Integer minStarRating,
            Pageable pageable
    );
}