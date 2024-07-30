package com.yp.puppy.api.dto.response.hotel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.hotel.HotelImage;
import com.yp.puppy.api.entity.hotel.ImageType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDto {

    @JsonProperty("hotelImgUri")
    private String hotelImgUri;

    @JsonProperty("type")
    private ImageType type;

    public ImageDto(HotelImage hotelImage) {
        this.hotelImgUri = hotelImage.getHotelImgUri();
        this.type = hotelImage.getType();
    }
}
