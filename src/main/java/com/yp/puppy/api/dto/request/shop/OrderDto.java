package com.yp.puppy.api.dto.request.shop;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Builder
public class OrderDto {

    private String cartId;
    private String userId;  // 사용자 ID 추가
    private int postNum;
    private String address;
    private String addressDetail;
    private String phoneNumber;

}
