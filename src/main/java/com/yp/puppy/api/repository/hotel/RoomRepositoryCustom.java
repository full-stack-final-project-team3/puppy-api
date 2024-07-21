package com.yp.puppy.api.repository.hotel;

import com.yp.puppy.api.entity.hotel.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomRepositoryCustom {

    Page<Room> findRooms(Pageable pageable, String sort);


}
