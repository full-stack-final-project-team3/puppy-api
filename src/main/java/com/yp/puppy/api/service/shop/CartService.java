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
        cartRepository.save(cart);
    }

    // 2. 유저의 장바구니 정보 조회 중간 처리
    public Cart getCart(String userId) {

        // PENDING 상태인 장바구니만 조회
        User user = userRepository.findById(userId).orElseThrow();

        return user.getCart();
    }

    // 3. 번들 구독 상태 변경 중간 처리 (유저가 옵션을 수정하면)
    public void updateCheckOutInfoCart(String userId, UpdateBundleDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Cart cart = user.getCart();

        String bundleId = dto.getBundle_id();

        // 장바구니에서 번들 목록 가져오기
        List<Bundle> bundles = cart.getBundles();

        // 일치하는 번들 찾기 및 상태 변경
        for (Bundle bundle : bundles) {
            if (bundle.getId().equals(bundleId)) {
                bundle.setSubsType(dto.getSubsType()); // 원하는 상태로 변경
                break; // 일치하는 번들을 찾았으므로 반복 종료
            }
        }

        cartRepository.save(cart);
    }

    // 4. 번들 삭제 중간 처리
    public void deleteBundle(String bundleId) {

        Bundle bundle = bundleRepository.findById(bundleId).orElseThrow(() ->
                new EntityNotFoundException("Bundle not found"));

        Dog dog = bundle.getDog();

        if (dog != null) {
            dog.setBundle(null);
            dogRepository.save(dog);
        }
        
        bundleRepository.deleteById(bundleId);
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

        return cart;
    }

    private Long calculateTotalPrice(List<Bundle> bundles) {
        return bundles.stream()
                .mapToLong(Bundle::getBundlePrice)
                .sum();
    }

}
