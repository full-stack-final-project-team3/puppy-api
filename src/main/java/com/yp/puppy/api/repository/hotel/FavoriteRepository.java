package com.yp.puppy.api.repository.hotel;

import com.yp.puppy.api.entity.hotel.Favorite;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, String> {
    List<Favorite> findByUser(User user);
    List<Favorite> findByHotel(Hotel hotel);
    Favorite findByUserAndHotel(User user, Hotel hotel);
}
