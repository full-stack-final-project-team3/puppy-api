package com.yp.puppy.api.repository.hotel;

import com.yp.puppy.api.entity.hotel.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, String>, HotelRepositoryCustom {

    @Query("SELECT h FROM Hotel h WHERE :location IS NULL OR SUBSTRING(h.location, 1, 3) = :location")
    List<Hotel> findHotels(@Param("location") String location, Sort sort);
}
