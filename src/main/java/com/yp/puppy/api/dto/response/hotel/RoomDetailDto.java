package com.yp.puppy.api.dto.response.hotel;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.hotel.HotelImage;
import com.yp.puppy.api.entity.hotel.Room;
import com.yp.puppy.api.entity.hotel.RoomType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDetailDto {

    private String roomId;
    private String name; // 객실 이름
    private String content; // 객실 내용
    private RoomType type; // 객실 타입 (소형견, 중형견, 대형견)
    private long price; // 객실 가격

    @JsonProperty("room-images")
    private List<HotelImage> roomImage; // 호텔 이미지 목록

    public RoomDetailDto(Room room) {
        this.roomId = room.getRoomId();
        this.name = room.getName();
        this.content = room.getContent();
        this.type = room.getType();
        this.price = room.getPrice();
        this.roomImage = room.getImages();

    }

}
