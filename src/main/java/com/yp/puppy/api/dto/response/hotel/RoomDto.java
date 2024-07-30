package com.yp.puppy.api.dto.response.hotel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.hotel.Room;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RoomDto {

    @JsonProperty("room-id")
    private String roomId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("content")
    private String content;

    @JsonProperty("type")
    private String type;

    @JsonProperty("price")
    private long price;

    @JsonProperty("room-images")
    private List<ImageDto> roomImage; // 호텔 이미지 목록

    // Room 엔티티로부터 RoomDto p를 생성하는 생성자
    public RoomDto(Room room) {
        this.roomId = room.getRoomId();
        this.name = room.getName();
        this.content = room.getContent();
        this.type = room.getType().name();
        this.price = room.getPrice();
        this.roomImage = room.getImages().stream()
                .map(ImageDto::new)
                .collect(Collectors.toList());
    }
}
