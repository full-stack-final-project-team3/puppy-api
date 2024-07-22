package com.yp.puppy.api.dto.response.hotel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.hotel.Room;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString(exclude = "rooms")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class HotelOneDto {

    @JsonProperty("hotel-id")
    private String hotelId; // 호텔 아이디

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

    @JsonProperty("room")
    private List<RoomDto> rooms;

    public HotelOneDto(Hotel hotel) {
        this.hotelId = hotel.getHotelId();
        this.name = hotel.getName();
        this.description = hotel.getDescription();
        this.businessOwner = hotel.getBusinessOwner();
        this.location = hotel.getLocation();
        this.rulesPolicy = hotel.getRulesPolicy();
        this.cancelPolicy = hotel.getCancelPolicy();
        this.price = hotel.getPrice();
        this.phoneNumber = hotel.getPhoneNumber();
        this.hotelImages = hotel.getImages().stream()
                .map(ImageDto::new)
                .collect(Collectors.toList());
        this.rooms = hotel.getRooms().stream()
                .map(RoomDto::new)
                .collect(Collectors.toList());
    }
}
