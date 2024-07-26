package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.dto.request.shop.TreatsSaveDto;
import com.yp.puppy.api.dto.response.shop.TreatsDetailDto;
import com.yp.puppy.api.dto.response.shop.TreatsListDto;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.Role;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.TreatsRepository;
import com.yp.puppy.api.repository.user.DogRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.yp.puppy.api.auth.TokenProvider.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TreatsService {

    private final TreatsRepository treatsRepository;
    private final UserRepository userRepository;
    private final DogRepository dogRepository;

    // 0. 유저의 강아지 정보 출력하기
    public List<Dog> showUsersDogList(TokenUserInfo userInfo) {

        String userId = userInfo.getUserId();

        User user = userRepository.findById(userId).orElseThrow();

        List<Dog> dogList = user.getDogList();

        return dogList;
    }

    // 1. 상품 전체 조회 중간처리
    public Map<String, Object> getTreatsList(TokenUserInfo userInfo, String dogId, int pageNo, String sort) {

        Dog userDogInfo = dogRepository.findById(dogId).orElseThrow();

        // 유저의 알레르지 리스트
        List<Dog.Allergy> dogInfoAllergies = userDogInfo != null ? userDogInfo.getAllergies() : null;

        System.out.println("\n@@@@@@@@@@@@@@@@@@\n" + dogInfoAllergies + "\nn@@@@@@@@@@@@@@@@@@\n");

        List<Treats.Allergic> allergics = convertDogAllergiesToTreatsAllergies(dogInfoAllergies);

        System.out.println("\n@@@@@@@@@@@@@@@@@@\n" + allergics + "\nn@@@@@@@@@@@@@@@@@@\n");

        List<Treats> all = treatsRepository.findAll();

        System.out.println("\n@@@@@@@@@@@@@@@@@@간식간식간식간식간식\n" + all + "\nn@@@@@@@@@@@@@@@@@@\n");

        Pageable pageable = PageRequest.of(pageNo - 1, 5);

        Page<Treats> treatsPage = treatsRepository.findTreats(allergics, pageable, sort);

        List<Treats> treatsList = treatsPage.getContent();

        System.out.println("\n@@@@@@@@@@@@@@@@@@\n" + treatsList + "\nn@@@@@@@@@@@@@@@@@@\n");

        List<TreatsListDto> treatsDtoList = new ArrayList<>();
        for (Treats treats : treatsList) {
            TreatsListDto treatsListDto = new TreatsListDto(treats);
            treatsDtoList.add(treatsListDto);
        }

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

    // 3. 상품 생성 중간처리
    public void saveTreats(TreatsSaveDto dto, String userId) {
        // 회원정보조회 (관리자냐?)
        User admin = userRepository.findById(userId).orElseThrow();

        // 권한에 따른 글쓰기 제한
        if (admin.getRole() != Role.ADMIN) throw new IllegalStateException("관리자만 등록을 할 수 있습니다.");

        Treats newTreats = dto.toEntity();

        Treats saveTreats = treatsRepository.save(newTreats);

        log.info("hotel: {}", saveTreats);
    }

    // 4. 상품 삭제 중간처리
    public void deleteTreat(String treatsId) {
        treatsRepository.deleteById(treatsId);
    }

    // 5. 상품 정보 수정 중간처리
    public void updateTreat(TreatsSaveDto dto, String treatsId) {
        Treats foundTreats = treatsRepository.findById(treatsId).orElseThrow();

        foundTreats.changeTreats(dto);

        treatsRepository.save(foundTreats);
    }

    public List<Treats.Allergic> convertDogAllergiesToTreatsAllergies(List<Dog.Allergy> dogAllergies) {
        if (dogAllergies == null) {
            return Collections.emptyList();
        }

        return dogAllergies.stream()
                .map(this::mapToTreatsAllergy)
                .collect(Collectors.toList());
    }

    private Treats.Allergic mapToTreatsAllergy(Dog.Allergy dogAllergy) {
        switch (dogAllergy) {
            case BEEF:
                return Treats.Allergic.BEEF;
            case CHICKEN:
                return Treats.Allergic.CHICKEN;
            case CORN:
                return Treats.Allergic.CORN;
            case DAIRY:
                return Treats.Allergic.DAIRY;
            case FISH:
                return Treats.Allergic.FISH;
            case FLAX:
                return Treats.Allergic.FLAX;
            case LAMB:
                return Treats.Allergic.LAMB;
            case PORK:
                return Treats.Allergic.PORK;
            case TURKEY:
                return Treats.Allergic.TURKEY;
            case WHEAT:
                return Treats.Allergic.WHEAT;
            default:
                return null; // 매핑되지 않는 경우
        }
    }

}
