package com.yp.puppy.api.dto.response.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.shop.TreatsPic;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatsInBundleDto {

    private String id;
    private String title; // 제품 이름

    @JsonProperty("treats-pics")
    private List<TreatsPic> treatsPics; // 제품 이미지 목록

    public TreatsInBundleDto(Treats treats) {
        this.id = treats.getId();
        this.title = treats.getTreatsTitle();
        this.treatsPics = treats.getTreatsPics();
    }
}
