package com.yp.puppy.api.dto.response.hotel;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.hotel.HotelImage;
import com.yp.puppy.api.entity.hotel.Room;
import com.yp.puppy.api.entity.hotel.RoomType;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomOneDto {

    @JsonProperty("room-id")
    private String roomId;

    @JsonProperty("room_name")
    private String name; // 객실 이름

    @JsonProperty("room-content")
    private String content; // 객실 내용

    @JsonProperty("room-type")
    private RoomType type; // 객실 타입 (소형견, 중형견, 대형견)

    @JsonProperty("room-price")
    private long price; // 객실 가격


    @JsonProperty("room-images")
    private List<ImageDto> roomImages; // 호텔 이미지 목록

    public RoomOneDto(Room room){
        this.roomId = room.getRoomId();
        this.name = room.getName();
        this.content = room.getContent();
        this.type = room.getType();
        this.price = room.getPrice();
        this.roomImages = room.getImages()
                .stream()
                .map(ImageDto::new)
                .collect(Collectors.toList())
        ;
    }

}
