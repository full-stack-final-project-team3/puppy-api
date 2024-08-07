package com.yp.puppy.api.dto.response.hotel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.hotel.Favorite;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteDTO {
    private String favoriteId;
    private String hotelName;
    private String hotelId;   // 호텔 ID도 반환
    private String location; // 호텔 위치

    @JsonProperty("hotel-images")
    private List<ImageDto> hotelImages; // 호텔 이미지


    public FavoriteDTO(Favorite favorite) {
        this.favoriteId = favorite.getFavoriteId();
        this.hotelName = favorite.getHotel().getName();
        this.hotelId = favorite.getHotel().getHotelId();
        this.location = favorite.getHotel().getLocation();

        this.hotelImages = favorite.getHotel().getImages()
                .stream().
                map(ImageDto::new)
                .collect(Collectors.toList());
    }
}
