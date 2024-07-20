package com.yp.puppy.api.repository.hotel;

import com.yp.puppy.api.entity.hotel.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    Page<Hotel> findEvents(Pageable pageable, String sort);

}
