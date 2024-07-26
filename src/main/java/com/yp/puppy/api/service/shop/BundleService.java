package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.dto.request.shop.BundleCreateDto;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.BundleRepository;
import com.yp.puppy.api.repository.shop.TreatsRepository;
import com.yp.puppy.api.repository.user.DogRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void createBundle(String userEmail, String dogId, BundleCreateDto dto) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with id: " + dogId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        log.info("Creating bundle for user: {}", user.getEmail());

        List<Treats> treatsList = getTreatsList(dto);

        Bundle newBundle = Bundle.builder()
                .user(user)
                .bundlePrice(29900L)
                .bundleTitle("강아지 맞춤 간식 패키지")
                .dog(dog)
                .treats(treatsList)
                .build();

        log.info("Dog before saving: {}", dog);
        log.info("Bundle before saving: {}", newBundle);

        // Bundle 저장
        Bundle savedBundle = bundleRepository.save(newBundle);

        updateDogBundle(dog, savedBundle);
    }

    // 2. 번들 삭제 중간 처리
    public void deleteBundle(String bundleId) {

        bundleRepository.deleteById(bundleId);

    }

    // 3. 번들 수정 중간 처리

    // 강아지의 번들 정보 추가
    private void updateDogBundle(Dog dog, Bundle savedBundle) {
        // Dog에 Bundle 설정
        dog.setBundle(savedBundle);

        // Dog 저장
        dogRepository.save(dog);
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
}
