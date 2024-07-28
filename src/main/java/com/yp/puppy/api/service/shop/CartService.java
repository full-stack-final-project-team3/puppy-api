package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.dto.request.shop.UpdateBundleDto;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.shop.Cart;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.BundleRepository;
import com.yp.puppy.api.repository.shop.CartRepository;
import com.yp.puppy.api.repository.user.DogRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BundleRepository bundleRepository;
    private final DogRepository dogRepository;

    // 1. 유저가 생성한 번들을 포함한 장바구니 생성하기 중간 처리
    public void createUserCart(String userId) {

        User user = userRepository.findById(userId).orElseThrow();

        Cart cart = createCart(user);

        System.out.println("@@@@@@@@@@@@@@@@@@@cart = \n\n\n" + cart);

        user.setCart(cart);

        userRepository.save(user);

    }

    public Cart getCart(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow();

        Cart cart = user.getCart();

        log.info("Retrieved Cart: {}", cart.getBundles());

        return cart;
    }

    // 5. 장바구니 비우기
    public void deleteCart(String cartId) {

        Cart cart = cartRepository.findById(cartId).orElseThrow();

        List<Bundle> bundles = cart.getBundles();

        for (Bundle bundle : bundles) {
            bundle.setCart(null);
            bundle.getDog().setBundle(null);
        }

        User user = cart.getUser();

        user.setCart(null);

        cartRepository.deleteById(cartId);

    }

    private Cart createCart(User user) {

        // 유저가 생성한 번들 중 상태가 PENDING인 번들만 필터링
        List<Bundle> createdBundles = user.getCreatedBundles().stream()
                .filter(bundle -> bundle.getBundleStatus() == Bundle.BundleStatus.PENDING) // 상태가 PENDING인 번들만 선택
                .collect(Collectors.toList());

        Long totalPrice = calculateTotalPrice(createdBundles);

        Cart cart = new Cart();
        cart.setTotalPrice(totalPrice);
        cart.setUser(user);
        cart.setBundles(createdBundles);
        cart.setCartStatus(Cart.CartStatus.PENDING);

        cartRepository.save(cart);

        for (Bundle bundle : createdBundles) {
            bundle.setCart(cart); // 각 Bundle의 cart 필드를 설정
        }

        return cart;
    }

    private Long calculateTotalPrice(List<Bundle> bundles) {
        return bundles.stream()
                .mapToLong(Bundle::getBundlePrice)
                .sum();
    }

}
