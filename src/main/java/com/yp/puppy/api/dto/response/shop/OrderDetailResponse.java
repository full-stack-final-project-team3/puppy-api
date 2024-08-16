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
        this.orderId = order.getId();
        this.orderDateTime = order.getOrderDateTime();
        this.address = order.getAddress();
        this.addressDetail = order.getAddressDetail();
        this.receiverAddress = order.getAddress() +" "+ order.getAddressDetail();
        this.orderStatus = order.getOrderStatus();
        this.deliveryRequest = order.getDeliveryRequest();
        this.customRequest = order.getCustomRequest();
        this.receiverName = order.getReceiverName();
        this.receiverPhone = order.getReceiverPhone();
        this.point = order.getPoint();
        this.totalPrice = order.getTotalPrice();
    }
}
