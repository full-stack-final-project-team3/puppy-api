package com.yp.puppy.api.dto.request.hotel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.dto.response.hotel.ImageDto;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.hotel.HotelImage;
import com.yp.puppy.api.entity.hotel.Room;
import com.yp.puppy.api.entity.hotel.RoomType;
import lombok.*;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<ImageDto> roomImages; // List<ImageDto>로 변경

    @JsonProperty("hotel-id")
    private String hotelId;

    public Room toEntity() {
        Room room = Room.builder()
                .name(this.name)
                .content(this.content)
                .type(this.type)
                .price(this.price)
                .build();

        if (this.roomImages != null) {
            List<HotelImage> images = this.roomImages.stream()
                    .filter(dto -> dto.getHotelImgUri() != null && !dto.getHotelImgUri().isEmpty())
                    .map(dto -> {
                        HotelImage image = new HotelImage();
                        image.setHotelImgUri(dto.getHotelImgUri());
                        image.setType(dto.getType());
                        image.setRoom(room);
                        return image;
                    })
                    .collect(Collectors.toList());

            room.setImages(images);
        }

        return room;
    }
}