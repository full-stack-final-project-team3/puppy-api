package com.yp.puppy.api.dto.response.shop;

import com.yp.puppy.api.entity.shop.Order;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private String orderId;
    private LocalDateTime orderDateTime;
    private String address;
    private String addressDetail;
    private String receiverAddress;
    private Order.OrderStatus orderStatus;
    private String deliveryRequest; // 배송 요청 사항
    private String customRequest; // 기타 요청 사항
    private String receiverName; //받는 사람 이름
    private String receiverPhone; //받는 사람 연락처
    private Long point; //포인트 사용
    private Long totalPrice; //총금액


    // 오다  객체로부터 오더리스폰스 객체를 생성하는 생송자
    public OrderDetailResponse(Order order) {
        this.orderId = order.getId();  // Order 객체에서 주문 ID 가져오기
        this.orderDateTime = order.getOrderDateTime();  // 오더 객체에서 주문 날짜랑 시간 가져옴
        this.address = order.getAddress();  // Cart 객체에서 총 가격 가죠옴
        this.addressDetail = order.getAddressDetail(); // 여기서 값 설정
        this.receiverAddress = order.getAddress() +" "+ order.getAddressDetail();  // Cart 객체에서 총 가격 가죠옴
        this.orderStatus = order.getOrderStatus(); // 여기서 값 설정
        this.deliveryRequest = order.getDeliveryRequest(); // 여기서 값 설정
        this.customRequest = order.getCustomRequest(); // 여기서 값 설정
        this.receiverName = order.getReceiverName(); // 여기서 값 설정
        this.receiverPhone = order.getReceiverPhone(); // 여기서 값 설정
        this.point = order.getPoint(); // 여기서 값 설정
        this.totalPrice = order.getTotalPrice(); // 여기서 값 설정
    }
}
