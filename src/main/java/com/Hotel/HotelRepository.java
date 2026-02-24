package com.Hotel;

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
}