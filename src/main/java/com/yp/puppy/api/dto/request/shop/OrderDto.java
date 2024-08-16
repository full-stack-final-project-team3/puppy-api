package com.yp.puppy.api.dto.request.shop;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;

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
    private String deliveryRequest; // 배송 요청 사항 추가
    private String customRequest;    // 기타 요청 사항 추가

    private String receiverName; //받는 사람 이름
    private String receiverPhone; //받는 사람 연락처

    private Long pointUsage; //포인트
    private Long totalPrice; //총 결제금액
}
