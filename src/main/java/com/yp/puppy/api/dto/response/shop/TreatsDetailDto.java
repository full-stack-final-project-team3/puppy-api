package com.yp.puppy.api.dto.response.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.shop.TreatsDetailPic;
import com.yp.puppy.api.entity.shop.TreatsPic;
import com.yp.puppy.api.entity.user.Dog;
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
    private Treats.TreatsType treatsType;
    private Treats.Allergic allergieList;
    private Dog.DogSize dogSize;
    private Treats.TreatsAgeType treatsAgeType;
    private int weight; // 제품 무게

    @JsonProperty("treats-pics")
    private List<TreatsPic> treatsPics; // 제품 이미지 목록

    @JsonProperty("treats-detail-pics")
    private List<TreatsDetailPic> treatsDetailPics; // 제품 이미지 목록

    public TreatsDetailDto(Treats treats) {
        this.id = treats.getId();
        this.title = treats.getTreatsTitle();
        this.weight = treats.getTreatsWeight();
        this.treatsType = treats.getTreatsType();
        this.treatsAgeType = treats.getTreatsAgeType();
        this.dogSize = treats.getDogSize();
        this.treatsPics = treats.getTreatsPics();
        this.treatsDetailPics = treats.getTreatsDetailPics();
    }

}
