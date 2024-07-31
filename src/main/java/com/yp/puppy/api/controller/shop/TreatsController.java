package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.TreatsDetailPicDto;
import com.yp.puppy.api.dto.request.shop.TreatsPicDto;
import com.yp.puppy.api.dto.request.shop.TreatsSaveDto;
import com.yp.puppy.api.dto.response.shop.TreatsDetailDto;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.service.shop.TreatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.yp.puppy.api.auth.TokenProvider.*;

@RestController
@RequestMapping("/treats")
@Slf4j
@RequiredArgsConstructor
public class TreatsController {

    private final TreatsService treatsService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 0. 유저의 강아지 목록 보여주기
//    @GetMapping
//    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
//    public ResponseEntity<?> showDogList(@AuthenticationPrincipal TokenUserInfo userInfo) {
//
//        if (userInfo == null) {
//            return ResponseEntity.ok().body("로그인이 필요합니다.");
//        }
//
//        List<Dog> UsersDoglist = treatsService.showUsersDogList(userInfo);
//
//        return ResponseEntity.ok().body(UsersDoglist);
//
//        // UserDogList가 null이면 그냥 아무거나 추천? 개 등록 시키기?
//
//    }

    // 0. 관리자 모든 상품 조회

    // 1. 상품 전체 맞춤 조회

    @GetMapping("/list/{dogId}")
    public ResponseEntity<?> getTreatsList(@RequestParam(required = false, defaultValue = "name") String sort,
//                                           TokenUserInfo userInfo,
                                           @PathVariable String dogId,
                                           @RequestParam(defaultValue = "1") int pageNo) {

        // userInfo가 null이면 에러 개가 없다면 그냥 아무거나 / 개 정보 등록

        Map<String, Object> treatsList = treatsService.getTreatsList(dogId, pageNo, sort);
        if (treatsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(treatsList);

    }

    // 2. 제품 상세 조회

    @GetMapping("/{treatsId}")
    public ResponseEntity<?> getTreats(@PathVariable String treatsId) {
        try {
            TreatsDetailDto detailDto = treatsService.getTreatsDetail(treatsId);
            return ResponseEntity.ok().body(detailDto);
        } catch (Exception e) {
            log.warn("제품 ID로 조회하는 중 오류 발생 : {}", treatsId);
            return ResponseEntity.badRequest().body("잘못된 treatsId 입니다.");
        }

    }

    // 3 제품 생성

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> registerTreats(@AuthenticationPrincipal TokenUserInfo userInfo,
                                            @ModelAttribute TreatsSaveDto dto) {
        log.info("Received DTO: {}", dto);

        try {
            // Treats 객체 생성
            Treats treats = dto.toEntity(uploadDir);// 실제 저장할 경로로 변경
            // Treats 저장
            treatsService.saveTreats(treats, userInfo.getUserId());
            return ResponseEntity.ok().body("제품 생성 성공");
        } catch (IllegalArgumentException e) {
            log.error("유효성 검사 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body("유효하지 않은 입력입니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    // 4. 제품 삭제

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{treatsId}")
    public ResponseEntity<?> deleteTreats(@PathVariable String treatsId) {
        try {
            treatsService.deleteTreat(treatsId);
            return ResponseEntity.ok().body("삭제성공");
        } catch (Exception e) {
            log.warn("제품 삭제에 실패했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("제품을 찾지 못햇습니다..");
        }
    }

    // 5. 제품 수정

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{treatsId}")
    public ResponseEntity<?> modifyTreats(@RequestBody TreatsSaveDto dto,
                                          @PathVariable String treatsId) {
        try {
            treatsService.updateTreat(dto, treatsId);
            return ResponseEntity.ok().body("수정 성공");
        } catch (Exception e) {
            log.warn("수정에 실패했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("수정할 제품을 찾지 못했습니다..");
        }
    }

}