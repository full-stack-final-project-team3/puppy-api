package com.yp.puppy.api.service.shop;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TreatsService {

    private final TreatsRepository treatsRepository;
    private final UserRepository userRepository;
    private final DogRepository dogRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 0. 관리자 상품 전체 조회 중간처리
    public List<TreatsListDto> getAllTreatsList(String userId) {

        User user = userRepository.findById(userId).orElseThrow();

        if (user.getRole() != Role.ADMIN) throw new IllegalStateException("관리자만 할 수 있습니다.");

        List<Treats> all = treatsRepository.findAll();

        List<TreatsListDto> treatsDtoList = new ArrayList<>();
        for (Treats treats : all) {
            TreatsListDto treatsListDto = new TreatsListDto(treats);
            treatsDtoList.add(treatsListDto);
        }

        return treatsDtoList;
    }

    // 1. 맞춤 상품 전체 조회 중간처리
    public Map<String, Object> getTreatsList(String dogId, Integer pageNo, String sort) {
        // 사용자 개 정보 조회
        Dog userDogInfo = dogRepository.findById(dogId).orElseThrow();
        Dog.DogSize dogSize = userDogInfo.getDogSize();
        Dog.DogAgeType dogAgeType = userDogInfo.getDogAgeType();
        List<Dog.Allergy> dogInfoAllergies = userDogInfo != null ? userDogInfo.getAllergies() : null;
        List<Treats.Allergic> allergies = convertDogAllergiesToTreatsAllergies(dogInfoAllergies);

        List<Treats> treatsList;
        long totalElements;

        if (pageNo != null && pageNo > 0) {
            // 페이징 처리
            Pageable pageable = PageRequest.of(pageNo - 1, 3);
            Page<Treats> treatsPage = treatsRepository.findTreats(allergies, dogSize, dogAgeType, pageable, sort);
            treatsList = treatsPage.getContent();
            totalElements = treatsPage.getTotalElements();
        } else {
            // 전체 리스트 조회
            treatsList = treatsRepository.findAllTreats(allergies, dogSize, dogAgeType, sort);
            totalElements = treatsList.size();
        }

        List<TreatsListDto> treatsDtoList = new ArrayList<>();
        for (Treats treats : treatsList) {
            TreatsListDto treatsListDto = new TreatsListDto(treats);
            treatsDtoList.add(treatsListDto);
        }

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
    public void saveTreats(Treats treats, String userId) {

        User admin = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        // 권한에 따른 글쓰기 제한
        if (admin.getRole() != Role.ADMIN) throw new IllegalStateException("관리자만 등록을 할 수 있습니다.");

        Treats savedTreats = treatsRepository.save(treats);
        log.info("treats: {}", savedTreats);
    }

    // 4. 상품 삭제 중간처리
    public void deleteTreat(String treatsId) {
        treatsRepository.deleteById(treatsId);
    }

    // 5. 상품 정보 수정 중간처리
    public void updateTreat(TreatsSaveDto dto, String treatsId) {
        Treats foundTreats = treatsRepository.findById(treatsId).orElseThrow();

        foundTreats.changeTreats(dto, uploadDir);

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
            case SOY:
                return Treats.Allergic.SOY;
            case RICE:
                return Treats.Allergic.RICE;
            case PEANUT:
                return Treats.Allergic.PEANUT;
            case BARLEY:
                return Treats.Allergic.BARLEY;
            case OAT:
                return Treats.Allergic.OAT;
            case POTATO:
                return Treats.Allergic.POTATO;
            case TOMATO:
                return Treats.Allergic.TOMATO;
            case SALMON:
                return Treats.Allergic.SALMON;
            case DUCK:
                return Treats.Allergic.DUCK;
            default:
                return null; // 매핑되지 않는 경우
        }
    }


}


