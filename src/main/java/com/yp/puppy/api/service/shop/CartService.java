package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.dto.request.shop.UpdateBundlesDto;
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

        if (user.getCart() != null) {
            cartRepository.deleteById(user.getCart().getId());
        }

        Cart cart = createCart(user);

        user.setCart(cart);

        userRepository.save(user);

    }

    // 2. 장바구니 조회
    public Cart getCart(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cart cart = user.getCart();

        if (cart == null) {
            cart = new Cart(); // 빈 장바구니 생성
            cart.setCartStatus(Cart.CartStatus.PENDING);
            cart.setUser(user);
            user.setCart(cart); // 사용자에게 빈 장바구니 할당
            cartRepository.save(cart);
            userRepository.save(user); // 사용자 정보 저장
            log.info("새로운 빈 장바구니 생성");
        } else {
            log.info("Retrieved Cart: {}", cart.getBundles());
        }

        return cart;
    }

    // 3. 번들 구독 상태 변경 중간 처리 (유저가 옵션을 수정하면)
    public Cart updateSubsInfoCart(String userId, UpdateBundlesDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.: " + userId));

        Cart cart = user.getCart();

        List<Bundle> bundles = cart.getBundles();

        for (UpdateBundlesDto.BundleUpdateDto bundleUpdate : dto.getBundles()) {
            String bundleId = bundleUpdate.getBundleId();
            // 일치하는 번들 찾기
            for (Bundle bundle : bundles) {
                if (bundle.getId().equals(bundleId)) {
                    bundle.setSubsType(bundleUpdate.getSubsType()); // 구독 타입 변경
                    break; // 일치하는 번들을 찾았으므로 반복 종료
                }
            }
        }

        cartRepository.save(cart);

        return user.getCart();
    }


    // 4. 번들 삭제 중간 처리
    public Cart deleteBundle(String userId, String bundleId) {

        User user = userRepository.findById(userId).orElseThrow();

        Bundle bundle = bundleRepository.findById(bundleId).orElseThrow(() ->
                new EntityNotFoundException("Bundle not found"));

        Cart cart = bundle.getCart();
        if (cart != null) {
            // 장바구니에서 번들을 제거
            List<Bundle> bundles = cart.getBundles();
            bundle.setCart(null);
            bundles.remove(bundle); // 번들 제거
            Long totalPrice = calculateTotalPrice(bundles);
            cart.setTotalPrice(totalPrice);
            cart.setBundles(bundles); // 장바구니에 업데이트된 번들 리스트 설정
            cartRepository.save(cart);
        }

        // 번들이 속한 개를 가져오기
        Dog dog = bundle.getDog();

        if (dog != null) {
            dog.setBundle(null);
            dogRepository.save(dog);
        }

        bundle.setCart(null);

        bundleRepository.deleteById(bundleId);

        return user.getCart();
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

        for (Bundle createdBundle : createdBundles) {
            createdBundle.setCart(cart);
            bundleRepository.save(createdBundle);
        }

        return cart;
    }

    private Long calculateTotalPrice(List<Bundle> bundles) {
        return bundles.stream()
                .mapToLong(Bundle::getBundlePrice)
                .sum();
    }

}
