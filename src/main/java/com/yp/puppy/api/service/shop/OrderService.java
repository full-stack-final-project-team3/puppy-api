package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.dto.request.shop.OrderDto;
import com.yp.puppy.api.entity.shop.Cart;
import com.yp.puppy.api.entity.shop.Cart.CartStatus;
import com.yp.puppy.api.entity.shop.Order;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.shop.Subscriptions;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.CartRepository;
import com.yp.puppy.api.repository.shop.OrderRepository;
import com.yp.puppy.api.repository.shop.BundleRepository;
import com.yp.puppy.api.repository.shop.SubscriptionsRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    public Order createOrder(OrderDto orderDto) {
        try {
            // 사용자 가져오기
            User user = userRepository.findById(orderDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없다"));

            // 사용자의 장바구니 가져오기
            Cart cart = user.getCart();
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
                    bundle.setBundleStatus(BundleStatus.ORDERED);
                    Subscriptions subs = setSubsDateBundle(bundle);
                    subscriptionsRepository.save(subs);
                    bundleRepository.save(bundle);
                }
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
            orderRepository.save(order);

            return order;
        } catch (Exception e) {
            log.error("주문 실패", e);
            throw new RuntimeException("주문 실패 : " + e.getMessage(), e);
        }
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
