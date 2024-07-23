package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.TreatsSaveDto;
import com.yp.puppy.api.dto.response.shop.TreatsDetailDto;
import com.yp.puppy.api.service.shop.TreatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.yp.puppy.api.auth.TokenProvider.*;

@RestController
@RequestMapping("/shop")
@Slf4j
@RequiredArgsConstructor
public class ShopController {

    private final TreatsService treatsService;

    // 1. 상품 전체 조회

    /**
     * @param sort   정렬 기준 (기본값 'name')
     * @param pageNo 페이지 번호 (기본값 1)
     * @return 간식 목록
     */
    @GetMapping("/treats")
    public ResponseEntity<?> list(@RequestParam(required = false, defaultValue = "name") String sort,
                                   TokenUserInfo userInfo,
                                  @RequestParam(defaultValue = "1") int pageNo) {

        Map<String, Object> treatsList = treatsService.getTreatsList(userInfo, pageNo, sort);
        if (treatsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(treatsList);

    }


    // 2. 제품 상세 조회
    /**
     * 제품 상세 정보를 조회합니다.
     * @param treatsId URL 경로에서 제공된 제품의 식별자
     * @return 제품의 상세 정보를 반환합니다. 제품 ID가 유효하지 않으면, 400 Bad Request 를 반환.
     */
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
    /**
     * @param userInfo 사용자 정보
     * @param dto 제품 저장 데이터 전송 객체
     * @return 생성 성공 메시지 또는 오류 메시지
     * @PreAuthorize 관리자만 작성가능
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<?> register(@AuthenticationPrincipal TokenUserInfo userInfo,
                                      @RequestBody TreatsSaveDto dto) {
        try {
            treatsService.saveTreats(dto, userInfo.getUserId());
            return ResponseEntity.ok().body("제품 생성 성공");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            // 401 권한이 안된다 임마
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
