package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.dto.request.shop.BundleCreateDto;
import com.yp.puppy.api.dto.request.shop.UpdateBundleDto;
import com.yp.puppy.api.dto.response.shop.TreatsInBundleDto;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.shop.Cart;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.BundleRepository;
import com.yp.puppy.api.repository.shop.CartRepository;
import com.yp.puppy.api.repository.shop.TreatsRepository;
import com.yp.puppy.api.repository.user.DogRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BundleService {

    private final BundleRepository bundleRepository;
    private final UserRepository userRepository;
    private final TreatsRepository treatsRepository;
    private final DogRepository dogRepository;
    private final CartRepository cartRepository;

    public void createBundle(String userEmail, String dogId, BundleCreateDto dto) {

        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with id: " + dogId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        // 강아지가 이미 번들을 가지고 있는지 확인
        if (dog.getBundle() != null) {
            throw new IllegalArgumentException("이미 번들을 가지고 있습니다.: " + dogId);
        }

        // 번들을 구성하는 간식 리스트를 가져옮
        List<Treats> treatsList = getTreatsList(dto);

        // 간식 리스트들을 간소화 후에 번들에 저장함
        List<Treats> treatsInBundleDtos = treatsInBundle(treatsList);

        Bundle newBundle = Bundle.builder()
                .user(user)
                .bundlePrice(29900L)
                .bundleTitle("강아지 맞춤 간식 패키지")
                .dog(dog)
                .treats(treatsInBundleDtos)
                .bundleStatus(Bundle.BundleStatus.PENDING)
                .build();

        log.info("Bundle before saving: {}", newBundle);

        // Bundle 저장
        Bundle savedBundle = bundleRepository.save(newBundle);

        log.info("@@@@@@@@@@@@@@@@@@@@savedBundle = \n\n\n {}", savedBundle);

        dog.setBundle(savedBundle);

        dogRepository.save(dog);

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

    // 제품리스트 가져오기
    private List<Treats> getTreatsList(BundleCreateDto dto) {
        List<Treats> treatsList = new ArrayList<>();
        if (dto.getTreatIds() != null) {
            for (String treatId : dto.getTreatIds()) {
                Treats treat = treatsRepository.findById(treatId)
                        .orElseThrow(() -> new IllegalArgumentException("Treat not found with id: " + treatId));
                treatsList.add(treat);
            }
        }
        return treatsList;
    }

    // 번들안에 있는 간식 정보를 간소화
    private List<Treats> treatsInBundle(List<Treats> treatsList) {

        List<Treats> treatsInBundleList = new ArrayList<>();

        for (Treats treats : treatsList) {
            treats.TreatsInBundleDto(treats);
            treatsInBundleList.add(treats);
        }

        return treatsInBundleList;

    }
}
