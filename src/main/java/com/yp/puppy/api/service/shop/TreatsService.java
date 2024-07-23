package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.dto.request.hotel.HotelSaveDto;
import com.yp.puppy.api.dto.request.shop.TreatsSaveDto;
import com.yp.puppy.api.dto.response.hotel.HotelOneDto;
import com.yp.puppy.api.dto.response.shop.TreatsDetailDto;
import com.yp.puppy.api.dto.response.shop.TreatsListDto;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.Role;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.TreatsRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.yp.puppy.api.auth.TokenProvider.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TreatsService {

    private final TreatsRepository treatsRepository;
    private final UserRepository userRepository;

    // 1. 상품 전체 조회 중간처리
    public Map<String, Object> getTreatsList(TokenUserInfo userInfo, int pageNo, String sort) {

        Dog userDogInfo = findUserDogInfo(userInfo);

        Pageable pageable = PageRequest.of(pageNo - 1, 5);

        Page<Treats> treatsPage = treatsRepository.findTreats(userDogInfo, pageable, sort);

        List<Treats> treatsList = treatsPage.getContent();

        List<TreatsListDto> treatsDtoList = treatsList.stream()
                .map(TreatsListDto::new)
                .collect(Collectors.toList());

        // 렌더링 될 개수
        long totalElements = treatsPage.getTotalElements();

        Map<String, Object> map = new HashMap<>();
        map.put("treatsList", treatsDtoList);
        map.put("totalCount", totalElements);

        return map;
    }

    // 2. 상품 상세 조회 중간처리
    public TreatsDetailDto getTreatsDetail(String treatsId) {

        Treats treats = treatsRepository.findById(treatsId).orElseThrow();

        return new TreatsDetailDto(treats);
    }

    // 3. 제품 생성 중간처리
    public void saveTreats(TreatsSaveDto dto, String userId) {
        // 회원정보조회 (관리자냐?)
        User admin = userRepository.findById(userId).orElseThrow();

        // 권한에 따른 글쓰기 제한
        if (admin.getRole() != Role.ADMIN) throw new IllegalStateException("관리자만 등록을 할 수 있습니다.");

        Treats newTreats = dto.toEntity();

        Treats saveTreats = treatsRepository.save(newTreats);

        log.info("hotel: {}", saveTreats);
    }

    // 유저의 강아지 정보 찾기
    private Dog findUserDogInfo(TokenUserInfo userInfo) {

        String userEmail = userInfo.getEmail();

        User user = userRepository.findByEmail(userEmail).orElseThrow();

        List<Dog> dogList = user.getDogList();

        Dog userDogInfo = dogList.get(0);

        return userDogInfo;

    }


}
