package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.dto.request.shop.BundleCreateDto;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.BundleRepository;
import com.yp.puppy.api.repository.shop.TreatsRepository;
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

    public void createBundle(BundleCreateDto dto, String userId) {

        // 사용자 정보 조회
        User user = userRepository.findById(userId).orElseThrow();

        // 선택된 Treats 엔티티 조회 및 번들에 추가
        Bundle bundle = new Bundle();
        bundle.setUser(user);

        List<Treats> treatsList = new ArrayList<>();
        for (String treatId : dto.getTreatIds()) {
            Treats treat = treatsRepository.findById(treatId).orElseThrow();
            treatsList.add(treat);
        }

        bundle.setTreats(treatsList);

        bundleRepository.save(bundle);
    }

}
