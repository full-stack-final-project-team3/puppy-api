package com.yp.puppy.api.repository.hotel;

import com.yp.puppy.api.entity.hotel.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HotelRepositoryCustom {

    Page<Hotel> findHotels(Pageable pageable, String sort);


}
