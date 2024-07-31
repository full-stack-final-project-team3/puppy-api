package com.yp.puppy.api.controller.dog;

import com.yp.puppy.api.dto.request.dog.DogDetailDto;
import com.yp.puppy.api.dto.request.dog.DogModifyDto;
import com.yp.puppy.api.dto.request.dog.DogSaveDto;
import com.yp.puppy.api.dto.response.dog.DogResponseDto;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.service.dog.DogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/dog")
@Slf4j
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;

    /**
     * 강아지 등록
     * @param dogSaveDto - 클라이언트로부터 받은 입력값
     * @param email - 사용자 email
     * @return - 저장된 강아지 정보
     */
    @PostMapping("/register/{email}")
    public ResponseEntity<?> registerDog(@RequestBody DogSaveDto dogSaveDto,
                                         @PathVariable String email) {
        log.info("request dog : {}", dogSaveDto);
        try {
            Dog savedDog = dogService.saveDog(dogSaveDto, email);
            return ResponseEntity.ok(savedDog);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 강아지 단일 조회
     * @param dogId - 강아지 ID
     * @return - 강아지 정보
     */
    @GetMapping("/{dogId}")
    public ResponseEntity<?> getDog(@PathVariable String dogId) {
        log.info("request dog : {}", dogId);
        try {
            Dog dog = dogService.findDog(dogId);
            return ResponseEntity.ok(dog);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("존재하지 않는 dogId 입니다.");
        }
    }

    /**
     * 강아지 삭제
     * @param dogId - 강아지 ID
     * @return - 삭제 결과
     */
    @DeleteMapping("/{dogId}")
    public ResponseEntity<?> deleteDog(@PathVariable String dogId) {
        try {
            dogService.deleteDog(dogId);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("삭제 실패.");
        }
    }

    /**
     * 강아지 정보 수정
     * @param dogId - 강아지 ID
     * @param dto - 수정할 강아지 정보
     * @return - 수정된 강아지 정보
     */
    @PatchMapping("/{dogId}")
    public ResponseEntity<?> updateDog(@PathVariable String dogId, @RequestBody DogModifyDto dto) {
        dogService.modifyInMyPage(dogId, dto); // 마이페이지에서 수정하는 요청을 받는 dto (알러지 제외)
        return ResponseEntity.ok().body("수정 완료");
    }

    /**
     * 사용자 강아지 모두 조회
     * @param userId - 사용자 ID
     * @return - 강아지 리스트
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findMyDogs(@PathVariable String userId) {
            log.info("유저 아이디: {}", userId);
            List<Dog> myPuppies = dogService.findMyPuppies(userId);
            log.info("강아지 정보: {}", myPuppies);
            return ResponseEntity.ok(myPuppies);
    }

    @DeleteMapping("/allergy")
    public ResponseEntity<?> deleteAllergy(@RequestParam Dog.Allergy allergy, @RequestParam String dogId) {
        log.info("dog id and allergy : {} - {}", dogId, allergy);
        dogService.deleteAllergy(dogId, allergy);
        return ResponseEntity.ok().body("삭제 완료");
    }

}