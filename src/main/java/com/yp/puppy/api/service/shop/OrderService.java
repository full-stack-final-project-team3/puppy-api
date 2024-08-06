package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.dto.request.shop.OrderDto;
import com.yp.puppy.api.entity.shop.Cart;
import com.yp.puppy.api.entity.shop.Order;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.CartRepository;
import com.yp.puppy.api.repository.shop.OrderRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public Order createOrder(OrderDto orderDto) {
        // 장바구니 가져오기, 없으면 더미 데이터를 사용
        Cart cart = cartRepository.findById(orderDto.getCartId()).orElseGet(() -> {
            Cart dummyCart = new Cart();
            dummyCart.setId(orderDto.getCartId());
            dummyCart.setTotalPrice(10000L);  // 예시 값
            return dummyCart;
        });

        // 사용자 가져오기
        User user = userRepository.findById(cart.getUser().getId()).orElse(null);
        if (user != null && orderDto.getPhoneNumber() != null) {
            user.setPhoneNumber(orderDto.getPhoneNumber()); // 사용자 전화번호 업데이트
        }

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
        return orderRepository.save(order);
    }


}
