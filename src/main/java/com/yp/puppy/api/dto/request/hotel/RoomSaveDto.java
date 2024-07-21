package com.yp.puppy.api.dto.request.hotel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.hotel.HotelImage;
import com.yp.puppy.api.entity.hotel.Room;
import com.yp.puppy.api.entity.hotel.RoomType;
import lombok.*;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomSaveDto {

    @JsonProperty("room-name")
    private String name;

    @JsonProperty("room-content")
    private String content;

    @JsonProperty("room-type")
    private RoomType type;

    @JsonProperty("room-price")
    private long price;

    @JsonProperty("room-images")
    private List<HotelImage> roomImage;

    @JsonProperty("hotel-id")  // 이 필드를 추가
    private String hotelId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    public Room toEntity() {
        Room room = Room.builder()
                .name(this.name)
                .content(this.content)
                .type(this.type)
                .price(this.price)
                .build();

        for (HotelImage image : this.roomImage) {
            image.setRoom(room);
        }

        room.setImages(this.roomImage);

        return room;
    }
}
