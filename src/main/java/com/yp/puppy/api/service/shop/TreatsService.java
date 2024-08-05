package com.yp.puppy.api.service.shop;

import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.dto.request.shop.TreatsSaveDto;
import com.yp.puppy.api.dto.response.shop.TreatsDetailDto;
import com.yp.puppy.api.dto.response.shop.TreatsListDto;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.shop.TreatsDetailPic;
import com.yp.puppy.api.entity.shop.TreatsPic;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.Role;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.shop.TreatsRepository;
import com.yp.puppy.api.repository.user.DogRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import com.yp.puppy.api.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 1. 상품 전체 조회 중간처리
    public Map<String, Object> getTreatsList(String dogId, int pageNo, String sort) {

        Dog userDogInfo = dogRepository.findById(dogId).orElseThrow();
        Dog.DogSize dogSize = userDogInfo.getDogSize();
        LocalDate birthday = userDogInfo.getBirthday();
        List<Dog.Allergy> dogInfoAllergies = userDogInfo != null ? userDogInfo.getAllergies() : null;
        List<Treats.Allergic> allergics = convertDogAllergiesToTreatsAllergies(dogInfoAllergies);

        // TreatsType 결정
        Treats.TreatsType treatsType = getTreatsTypeByPageNumber(pageNo - 1);

        // 페이저블 기본 사이즈
        int defaultPageSize = 100;

        // 각 타입에 따른 간식 리스트 조회
        Page<Treats> initialTreatsPage = treatsRepository.findTreats(allergics, dogSize, PageRequest.of(0, defaultPageSize), sort, treatsType);

        log.info("initialTreatsPage: {}", initialTreatsPage);

        // 각 타입의 간식 리스트의 길이에 따라 페이지 사이즈 결정
        int actualSize = initialTreatsPage.getContent().size();

        // 최종 Pageable 설정
        Pageable pageable = PageRequest.of(pageNo - 1, actualSize > 0 ? actualSize : defaultPageSize);

        // 최종 간식 리스트 조회
        Page<Treats> treatsPage = treatsRepository.findTreats(allergics, dogSize, pageable, sort, treatsType);

        List<Treats> treatsList = treatsPage.getContent();

        List<TreatsListDto> treatsDtoList = new ArrayList<>();
        for (Treats treats : treatsList) {
            TreatsListDto treatsListDto = new TreatsListDto(treats);
            treatsDtoList.add(treatsListDto);
        }

        long totalElements = treatsPage.getTotalElements();

        Map<String, Object> map = new HashMap<>();
        map.put("treatsList", treatsDtoList);
        map.put("totalCount", totalElements);

        log.info("Fetching treats for page: {}, sort: {}, treatsType: {}", pageNo, sort, treatsType);

        return map;
    }

    // 2. 상품 상세 조회 중간처리
    public TreatsDetailDto getTreatsDetail(String treatsId) {

        Treats treats = treatsRepository.findById(treatsId).orElseThrow();

        return new TreatsDetailDto(treats);
    }

    // 3. 상품 생성 중간처리
    public void saveTreats(Treats treats, String userId) {
        // 회원정보 조회 (관리자냐?)
        User admin = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        // 권한에 따른 글쓰기 제한
        if (admin.getRole() != Role.ADMIN) throw new IllegalStateException("관리자만 등록을 할 수 있습니다.");

        // 데이터베이스에 저장
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

        foundTreats.changeTreats(dto);

        treatsRepository.save(foundTreats);
    }

    // 강아지 나이 계산 메서드
    private int calculateDogAge(LocalDate birthday) {
        if (birthday == null) {
            return 0; // 출생일이 없는 경우 기본값으로 0세를 반환
        }
        return LocalDate.now().getYear() - birthday.getYear();
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

    // 페이지 번호에 따른 TreatsType 결정 메서드
    private Treats.TreatsType getTreatsTypeByPageNumber(int pageNumber) {
        switch (pageNumber) {
            case 0:
                return Treats.TreatsType.DRY;   // 1페이지
            case 1:
                return Treats.TreatsType.WET;   // 2페이지
            case 2:
                return Treats.TreatsType.GUM;   // 3페이지
            case 3:
                return Treats.TreatsType.KIBBLE; // 4페이지
            case 4:
                return Treats.TreatsType.SUPPS;  // 5페이지
            default:
                return null; // 원하는 타입이 없을 경우
        }
    }

}


