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

    public Order createOrder(OrderDto orderDto) {
        try {
            // 사용자 가져오기
            User user = userRepository.findById(orderDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없다"));

            // 사용자의 장바구니 가져오기
            Cart cart = user.getCart();
            if (cart == null) {
                throw new RuntimeException("해당 유저의 장바구니를 찾을 수 없습니다.");
            }


            // 장바구니에서 번들 가져오기
            if (cart.getBundles().isEmpty()) {
                throw new RuntimeException("장바구니에 번들이 없습니다.");
            }

            Bundle bundle = cart.getBundles().get(0); // 첫 번째 번들 가져오기

            // 주문 객체 생성
            Order order = Order.builder()
                    .id(null)
                    .orderDateTime(LocalDateTime.now())
                    .postNum(orderDto.getPostNum())
                    .address(orderDto.getAddress())
                    .addressDetail(orderDto.getAddressDetail())
                    .orderStatus(Order.OrderStatus.PAID)
                    .user(user)
                    .cart(cart)
                    .build();

            // 주문 저장
            orderRepository.save(order);

            return order;
        } catch (Exception e) {
            log.error("주문 실패", e);
            throw new RuntimeException("주문 실패 : " + e.getMessage(), e);
        }
    }
}
