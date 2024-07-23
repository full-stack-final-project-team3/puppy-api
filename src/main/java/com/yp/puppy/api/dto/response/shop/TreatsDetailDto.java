package com.yp.puppy.api.dto.response.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.shop.TreatsDetailPic;
import lombok.*;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatsDetailDto {

    private String id;
    private String title; // 제품 이름
    private int weight; // 제품 무게

    @JsonProperty("treats-detail-pics")
    private List<TreatsDetailPic> treatsDetailPics; // 제품 이미지 목록

    public TreatsDetailDto(Treats treats) {
        this.id = treats.getId();
        this.title = treats.getTreatsTitle();
        this.weight = treats.getTreatsWeight();
        this.treatsDetailPics = treats.getTreatsDetailPics();
    }

}
