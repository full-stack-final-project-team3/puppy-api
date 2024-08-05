package com.yp.puppy.api.dto.request.dog;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 마이페이지에서 강아지 수정할때 받는 dto, 알러지는 따로 함
public class DogModifyDto {

    private double weight;
    private String dogProfileUrl; // 추가 예정

}
