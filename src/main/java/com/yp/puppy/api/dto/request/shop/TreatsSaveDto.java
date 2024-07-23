package com.yp.puppy.api.dto.request.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.shop.TreatsDetailPic;
import com.yp.puppy.api.entity.shop.TreatsPic;
import com.yp.puppy.api.entity.user.Allergy;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatsSaveDto {

    @JsonProperty("treats-title")
    private String title; // 제품 이름

    @JsonProperty("treats-type")
    private Treats.TreatsType treatsType; // 제품 타입

    @JsonProperty("treats-weight")
    private int treatsWeight; // 제품 무게

    @JsonProperty("treats-allergy")
    private List<Allergy> allergies; // 알러지 유발 항목

    @JsonProperty("treats-pics")
    private List<TreatsPic> treatsPics; // 제품 이미지 목록

    @JsonProperty("treats-detail-pics")
    private List<TreatsDetailPic> treatsDetailPics; // 제품 상세 이미지 목록

    public Treats toEntity() {
        Treats treats = Treats.builder()
                .treatsTitle(this.title)
                .treatsType(this.treatsType)
                .treatsWeight(this.treatsWeight)
                .allergies(this.allergies)
                .build();

        for (TreatsPic pic : this.treatsPics) {
            pic.setTreats(treats);
        }
        treats.setTreatsPics(this.treatsPics);

        for (TreatsDetailPic detailPic : this.treatsDetailPics) {
            detailPic.setTreats(treats);
        }
        treats.setTreatsDetailPics(this.treatsDetailPics);

        return treats;
    }
}
