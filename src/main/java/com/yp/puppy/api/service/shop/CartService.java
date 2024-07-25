package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.dto.request.shop.UpdateCartDto;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.shop.Cart;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.CartRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    // 1. 유저가 생성한 번들을 포함한 장바구니 생성하기 중간 처리
    public void createUserCart(String userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Cart cart = createCart(user);
        cartRepository.save(cart);
    }

    // 2. 유저의 장바구니 정보 조회 중간 처리
    public Cart getCart(String userId) {

        // PENDING 상태인 장바구니만 조회
        User user = userRepository.findById(userId).orElseThrow();

        return user.getCart();
    }

    // 3. 장바구니 상태 변경 중간 처리 (유저가 구매를 클릭하면)
    public void updateCart(String userId, UpdateCartDto dto) {

        User user = userRepository.findById(userId).orElseThrow();

        Cart cart = user.getCart();

        cart.setSubsType(dto.getSubsType());

        cartRepository.save(cart);
    }

    private Cart createCart(User user) {

        // 번들의 상태가 PENDING 인 번들만 가져와야함
        List<Bundle> createdBundles = user.getCreatedBundles();

        Long totalPrice = calculateTotalPrice(createdBundles);

        Cart cart = new Cart();
        cart.setTotalPrice(totalPrice);
        cart.setUser(user);
        cart.setBundles(createdBundles);
        cart.setCartStatus(Cart.CartStatus.PENDING);

        return cart;
    }

    private Long calculateTotalPrice(List<Bundle> bundles) {
        return bundles.stream()
                .mapToLong(Bundle::getBundlePrice)
                .sum();
    }

}
