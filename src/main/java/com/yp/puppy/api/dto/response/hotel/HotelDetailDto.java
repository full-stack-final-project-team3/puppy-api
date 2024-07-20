package com.yp.puppy.api.dto.response.hotel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.hotel.HotelImage;
import lombok.*;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDetailDto {

    private String id;
    private String name; // 호텔 이름
    private String description; // 호텔 설명
    private String location; // 호텔 위치
    private long price; // 호텔 가격
    private String phoneNumber; // 호텔 전화번호

    @JsonProperty("hotel-images")
    private List<HotelImage> hotelImages; // 호텔 이미지 목록


    public HotelDetailDto(Hotel hotel) {
        this.id = hotel.getHotelId();
        this.name = hotel.getName();
        this.description = hotel.getDescription();
        this.location = hotel.getLocation();
        this.price = hotel.getPrice();
        this.phoneNumber = hotel.getPhoneNumber();
        this.hotelImages = hotel.getImages();
    }

}
