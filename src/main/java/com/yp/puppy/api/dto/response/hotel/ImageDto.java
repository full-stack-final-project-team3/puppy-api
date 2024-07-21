package com.yp.puppy.api.dto.response.hotel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.hotel.HotelImage;
import com.yp.puppy.api.entity.hotel.ImageType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ImageDto {

    @JsonProperty("image-uri")
    private String imageUri;

    @JsonProperty("image-type")
    private ImageType imageType;

    public ImageDto(HotelImage hotelImage) {
        this.imageUri = hotelImage.getHotelImgUri();
        this.imageType = hotelImage.getType();
    }
}
