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

    // 1. 번들 생성 중간 처리
    public void createBundle(String userEmail, String dogId, BundleCreateDto dto) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new IllegalArgumentException("Dog not found with id: " + dogId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        Bundle bundle = new Bundle();
        bundle.setUser(user);
        bundle.setDog(dog);
        bundle.setBundlePrice(29900L); // 기본값 설정
        bundle.setBundleTitle("강아지 맞춤 간식 패키지"); // 기본값 설정

        log.info("Creating bundle for user: {}", user.getEmail());

        List<Treats> treatsList = new ArrayList<>();
        if (dto.getTreatIds() != null) {
            for (String treatId : dto.getTreatIds()) {
                Treats treat = treatsRepository.findById(treatId)
                        .orElseThrow(() -> new IllegalArgumentException("Treat not found with id: " + treatId));
                treatsList.add(treat);
            }
        }

        bundle.setTreats(treatsList);
        bundleRepository.save(bundle);

        // Bundle이 Dog에 설정되도록 추가
        dog.setBundle(bundle); // Dog의 bundle 필드에 Bundle 설정
        dogRepository.save(dog);

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@dog.getBundle() = \n\n" + dog.getBundle());
    }


    // 2. 번들 삭제 중간 처리
    public void deleteBundle(String bundleId) {

        bundleRepository.deleteById(bundleId);

    }

    // 3. 번들 수정 중간 처리
}
