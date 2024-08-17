package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.dto.request.shop.OrderDto;
import com.yp.puppy.api.dto.response.shop.OrderDetailResponse;
import com.yp.puppy.api.dto.response.shop.OrderResponse; // 기존 OrderResponse DTO 사용
import com.yp.puppy.api.entity.shop.Cart;
import com.yp.puppy.api.entity.shop.Cart.CartStatus;
import com.yp.puppy.api.entity.shop.Order;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.shop.Subscriptions;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.CartRepository;
import com.yp.puppy.api.repository.shop.OrderRepository;
import com.yp.puppy.api.repository.shop.BundleRepository;
import com.yp.puppy.api.repository.shop.SubscriptionsRepository;
import com.yp.puppy.api.repository.user.DogRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; // Stream API 사용을 위한 import

import static com.yp.puppy.api.entity.shop.Bundle.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BundleRepository bundleRepository;
    private final SubscriptionsRepository subscriptionsRepository;
    private final DogRepository dogRepository;

    public Order createOrder(OrderDto orderDto) {
        try {

            log.info("오더 디티오 배송요청 = {}, 기타요청 = {}",
                    orderDto.getDeliveryRequest(), orderDto.getCustomRequest()); // 전달된 디티오 데이터 확인

            // 사용자 가져오기
            User user = userRepository.findById(orderDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없다"));

            // 사용자의 장바구니 가져오기
            Cart cart = cartRepository.findById(orderDto.getCartId()).orElseThrow();
            if (cart == null) {
                throw new RuntimeException("해당 유저의 장바구니를 찾을 수 없습니다.");
            } else {
                cart.setCartStatus(CartStatus.ORDERED);
                cart.setPurchasedUserId(user.getId());
                cart.setUser(null);
                user.setCart(null);
                cartRepository.save(cart);
                userRepository.save(user);
            }

            // 장바구니에서 번들 가져오기
            if (cart.getBundles().isEmpty()) {
                throw new RuntimeException("장바구니에 번들이 없습니다.");
            } else {
                List<Bundle> bundles = cart.getBundles();
                for (Bundle bundle : bundles) {
                    Dog dog = bundle.getDog();
                    dog.setHasSubs(true);
                    bundle.setBundleStatus(BundleStatus.ORDERED);
                    Subscriptions subs = setSubsDateBundle(bundle);
                    dogRepository.save(dog);
                    subscriptionsRepository.save(subs);
                    bundleRepository.save(bundle);
                }
            }

            // Order 객체 생성 시 배송 요청 사항 및 기타 요청 사항 추가
            Order order = Order.builder()
                    .id(null)
                    .orderDateTime(LocalDateTime.now())
                    .postNum(orderDto.getPostNum())
                    .receiverName(orderDto.getReceiverName())
                    .receiverPhone(orderDto.getReceiverPhone())
                    .address(orderDto.getAddress())
                    .addressDetail(orderDto.getAddressDetail())
                    .orderStatus(Order.OrderStatus.PAID)  // 주문 상태를 PAID 로 설정
                    .user(user)
                    .cart(cart)
                    .deliveryRequest(orderDto.getDeliveryRequest()) // 배송 요청 사항 설정
                    .customRequest(orderDto.getCustomRequest())     // 기타 요청 사항 설정
                    .point(orderDto.getPointUsage())
                    .totalPrice(orderDto.getTotalPrice())
                    .build();
//            log.info("Saving Order: DeliveryRequest = {}, CustomRequest = {}",
//                    order.getDeliveryRequest(), order.getCustomRequest()); // 저장 직전 데이터 확인

            // 주문 저장
            orderRepository.save(order);

            return order;
        } catch (Exception e) {
            log.error("주문 실패", e);
            throw new RuntimeException("주문 실패 : " + e.getMessage(), e);
        }
    }


    // 주문 취소 메서드 추가}
    public void cancelOrder(String orderId) {
        // 로그 추가: 주문 아이디 확인
        log.info("해당 아이디로 주문을 취소하려고 한다!!아오!! : {}", orderId);

        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("왜!!!!주문을 못찾는데 아오진짜ㅓ"));

        // 주문 상태를 취소로 변경
        order.setOrderStatus(Order.OrderStatus.CANCELLED);

        // 변경된 주문 저장
        orderRepository.save(order);
    }


    public OrderDetailResponse getOrderDetail(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없디."));

        return new OrderDetailResponse(order);
    }

    public List<OrderResponse> getOrderHistory(String userId) {
        return orderRepository.findByUser(userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없디.")))
                .stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }


    // 각 번들의 구독 정보 업데이트
    private Subscriptions setSubsDateBundle(Bundle bundle) {

        Subscriptions subscriptions = new Subscriptions();

        subscriptions.setBundle(bundle);

        subscriptions.setSubscriptionsStartDate(LocalDateTime.now());

        if (bundle.getSubsType() == SubsType.ONE) {

            LocalDateTime oneMonthLater = LocalDateTime.now().plusMonths(1);
            subscriptions.setSubscriptionsEndDate(oneMonthLater);

        } else if (bundle.getSubsType() == SubsType.MONTH3) {

            LocalDateTime threeMonthLater = LocalDateTime.now().plusMonths(3);
            subscriptions.setSubscriptionsEndDate(threeMonthLater);

        } else if (bundle.getSubsType() == SubsType.MONTH6) {

            LocalDateTime sixMonthLater = LocalDateTime.now().plusMonths(6);
            subscriptions.setSubscriptionsEndDate(sixMonthLater);

        } else {
            System.out.println("지원하지 않는 구독 유형입니다.");
        }

        bundle.setSubscriptions(subscriptions);

        return subscriptions;
    }
}
