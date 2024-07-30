package com.yp.puppy.api.dto.request.hotel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.dto.response.hotel.ImageDto;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.hotel.HotelImage;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelSaveDto {

    @JsonProperty("hotel-name")
    private String name; // 호텔 이름

    @JsonProperty("description")
    private String description; // 호텔 설명

    @JsonProperty("business-owner")
    private String businessOwner; // 사업자 정보

    @JsonProperty("location")
    private String location; // 호텔 위치

    @JsonProperty("rules-policy")
    private String rulesPolicy; // 호텔 규칙

    @JsonProperty("cancel-policy")
    private String cancelPolicy; // 취소 규정

    @JsonProperty("price")
    private long price; // 호텔 가격

    @JsonProperty("phone-number")
    private String phoneNumber; // 호텔 전화번호

    @JsonProperty("hotel-images")
    private List<ImageDto> hotelImages; // 호텔 이미지 목록

    public Hotel toEntity() {
        Hotel hotel = Hotel.builder()
                .name(this.name)
                .description(this.description)
                .businessOwner(this.businessOwner)
                .location(this.location)
                .rulesPolicy(this.rulesPolicy)
                .cancelPolicy(this.cancelPolicy)
                .price(this.price)
                .phoneNumber(this.phoneNumber)
                .build();

        List<HotelImage> images = this.hotelImages.stream()
                .map(dto -> HotelImage.builder()
                        .type(dto.getType())
                        .hotelImgUri(dto.getHotelImgUri())
                        .hotel(hotel) // 역참조 설정
                        .build())
                .collect(Collectors.toList());

        hotel.setImages(images);

        return hotel;
    }
}
