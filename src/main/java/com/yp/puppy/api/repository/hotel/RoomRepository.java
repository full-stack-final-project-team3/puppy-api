package com.yp.puppy.api.repository.hotel;

import com.yp.puppy.api.entity.hotel.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, String> , RoomRepositoryCustom{

    List<Room> findByHotel_HotelId(String hotelId);
}
