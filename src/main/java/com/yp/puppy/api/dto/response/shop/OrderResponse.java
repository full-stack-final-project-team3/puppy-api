package com.yp.puppy.api.dto.response.shop;

import com.yp.puppy.api.entity.shop.Order;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.shop.Treats;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String orderId;
    private Order.OrderStatus orderStatus;
    private LocalDateTime orderDateTime;
    private Long totalPrice;  // 롱타입으로 변경함
    //private BundleResponse bundle;
    private List<BundleResponse> bundles;  // 리스트로 변경해가지구 순회하믄서 모든 번들을 렌더링 하려고 변경함
    private String deliveryRequest; // 배송 요청 사항 필드 추가

    // 오다  객체로부터 오더리스폰스 객체를 생성하는 생송자
    public OrderResponse(Order order) {
        this.orderId = order.getId();  // Order 객체에서 주문 ID 가져오기
        this.orderStatus = order.getOrderStatus();
        this.orderDateTime = order.getOrderDateTime();  // 오더 객체에서 주문 날짜랑 시간 가져옴
        this.totalPrice = order.getCart().getTotalPrice();  // Cart 객체에서 총 가격 가죠옴
        // Cart 객체에 포함된 모든 번들을 BundleResponse 리스트로 변환
        this.bundles = order.getCart().getBundles().stream()
                .map(BundleResponse::new)
                .collect(Collectors.toList());
        this.deliveryRequest = order.getDeliveryRequest(); // 여기서 값 설정
    }


    // 내부 스태틱 클래스 번들리스폰스
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BundleResponse {
        private String bundleId;
        private String bundleTitle;
        private Long bundlePrice;
        private String dogName;
        private String subsType;
        private LocalDateTime subscriptionsStartDate;
        private LocalDateTime subscriptionsEndDate;
        private int subscriptionsCycle;
        private List<TreatResponse> treats;  // 번들에 포함된 간식 리스트

        // Bundle 객체를 기반으로 BundleResponse 객체를 생성하는 생성자
        public BundleResponse(Bundle bundle) {
            this.bundleId = bundle.getId();  // Bundle 객체에서 번들 아이디 가져오기
            this.bundleTitle = bundle.getBundleTitle();
            this.bundlePrice = bundle.getBundlePrice();
            this.dogName = bundle.getDogName();
            this.subsType = bundle.getSubsType().name();
            this.subscriptionsStartDate = bundle.getSubscriptionsStartDate();
            this.subscriptionsEndDate = bundle.getSubscriptionsEndDate();
            this.subscriptionsCycle = bundle.getSubscriptionsCycle();
            this.treats = bundle.getTreats().stream()
                    .map(TreatResponse::new)  // 각 트릿 객체를 TreatResponse 로 벼난
                    .collect(Collectors.toList());  // 변환된 TreatResponse 객체들을 리스트로 수집함
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TreatResponse {
            private String treatId;
            private String treatTitle;

            public TreatResponse(Treats treats) {
                this.treatId = treats.getId();
                this.treatTitle = treats.getTreatsTitle();
            }
        }
    }
}
