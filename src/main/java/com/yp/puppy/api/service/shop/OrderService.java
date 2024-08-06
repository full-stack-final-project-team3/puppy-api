package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.dto.request.shop.OrderDto;
import com.yp.puppy.api.entity.shop.Cart;
import com.yp.puppy.api.entity.shop.Order;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.CartRepository;
import com.yp.puppy.api.repository.shop.OrderRepository;
import com.yp.puppy.api.repository.shop.BundleRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BundleRepository bundleRepository;

    public Order createOrder(OrderDto orderDto) {
        try {
            // 사용자 가져오기
            User user = userRepository.findById(orderDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

            // 더미 번들 생성
            Bundle dummyBundle = Bundle.builder()
                    .bundleTitle("세상에 나쁜 개는 없다 패키지")
                    .bundlePrice(887711L)
                    .user(user)
                    .build();
            // 반들 저장
            Bundle savedBundle = bundleRepository.save(dummyBundle);

            // 더미 장바구니 가져오는거 만약 업스면 더미 데이터를 사용해라
            Cart cart = cartRepository.findById(orderDto.getCartId()).orElseGet(() -> {
                Cart dummyCart = new Cart();
                dummyCart.setId(orderDto.getCartId());
                dummyCart.setTotalPrice(savedBundle.getBundlePrice());
                dummyCart.setBundles(Collections.singletonList(savedBundle));
                dummyCart.setCartStatus(Cart.CartStatus.PENDING);
                dummyCart.setUser(user);
                return dummyCart;
            });
            // 카트 저장
            Cart savedCart = cartRepository.save(cart);

            // 주문 객체 생성
            Order order = Order.builder()
                    .id(null)
                    .orderDateTime(LocalDateTime.now())
                    .postNum(orderDto.getPostNum())
                    .address(orderDto.getAddress())
                    .addressDetail(orderDto.getAddressDetail())
                    .orderStatus(Order.OrderStatus.PAID)
                    .user(user)
                    .cart(savedCart)
                    .build();
            // 주문 저장
            orderRepository.save(order);

            // 주문 객체와 함께 번들 정보 반환
            return order;
        } catch (Exception e) {
            log.error("Order creation failed", e);
            throw new RuntimeException("Order creation failed", e);
        }
    }
}
