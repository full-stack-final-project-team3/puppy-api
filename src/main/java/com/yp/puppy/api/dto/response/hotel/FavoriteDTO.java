package com.yp.puppy.api.dto.response.hotel;

import com.yp.puppy.api.entity.hotel.Favorite;
import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteDTO {
    private String favoriteId;
    private String hotelName; // 호텔 이름만 반환
    private String hotelId;   // 호텔 ID도 반환


    public FavoriteDTO(Favorite favorite) {
        this.favoriteId = favorite.getFavoriteId();
        this.hotelName = favorite.getHotel().getName();
        this.hotelId = favorite.getHotel().getHotelId();
    }
}
