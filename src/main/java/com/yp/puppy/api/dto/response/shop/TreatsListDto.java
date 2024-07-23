package com.yp.puppy.api.dto.response.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.hotel.HotelImage;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.shop.TreatsPic;
import lombok.*;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatsListDto {

    private String id;
    private Treats.TreatsType type; // 제품 타입
    private String title; // 제품 이름

    @JsonProperty("treats-pics")
    private List<TreatsPic> treatsPics; // 제품 이미지 목록

    public TreatsListDto(Treats treats) {
        this.id = treats.getId();
        this.title = treats.getTreatsTitle();
        this.type = treats.getTreatsType();
        this.treatsPics = treats.getTreatsPic();
    }
}
