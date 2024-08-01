package com.yp.puppy.api.service.hotel;

import com.yp.puppy.api.entity.hotel.Favorite;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.hotel.FavoriteRepository;
import com.yp.puppy.api.repository.hotel.HotelRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    public String addFavorite(String userId, String hotelId) throws IllegalStateException {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 호텔 ID입니다."));

        Favorite existingFavorite = favoriteRepository.findByUserAndHotel(user, hotel);
        if (existingFavorite != null) {
            throw new IllegalStateException("즐겨찾기가 이미 존재합니다.");
        }

        Favorite newFavorite = new Favorite(user, hotel);
        favoriteRepository.save(newFavorite);
        return "즐겨찾기가 성공적으로 추가되었습니다.";
    }

    public void removeFavorite(String userId, String hotelId) throws IllegalStateException {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 호텔 ID입니다."));

        Favorite favorite = favoriteRepository.findByUserAndHotel(user, hotel);
        if (favorite == null) {
            throw new IllegalStateException("즐겨찾기가 존재하지 않습니다.");
        }

        favoriteRepository.delete(favorite);
    }

    public List<Favorite> getFavoritesByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
        log.info("user: {}", user);
        return favoriteRepository.findByUser(user);
    }

    // 호텔삭제할떄 즐겨찾기먼저 삭제하고 해야해
    public void removeAllFavoritesByHotel(String hotelId) {
        List<Favorite> favorites = favoriteRepository.findByHotelHotelId(hotelId);
        if (!favorites.isEmpty()) {
            favoriteRepository.deleteAll(favorites);
        }
    }
}
