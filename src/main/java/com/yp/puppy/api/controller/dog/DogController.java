package com.yp.puppy.api.controller.dog;

import com.yp.puppy.api.dto.request.dog.DogSaveDto;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.service.dog.DogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dog")
@Slf4j
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;


    /**
     *                      강아지 등록
     * @param dogSaveDto - 클라이언트로부터 받은 입력값
     * @return -
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerDog(@RequestBody DogSaveDto dogSaveDto) {
        try {
            Dog savedDog = dogService.saveDog(dogSaveDto);
            return ResponseEntity.ok(savedDog);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{dogId}")
    public ResponseEntity<?> getDog(@PathVariable String dogId) {

        Dog dog = null;
        try {
            dog = dogService.findDog(dogId);
            return ResponseEntity.ok().body(dog);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("존재하지 않는 dogId 입니다.");
        }
    }

    @DeleteMapping("/{dogId}")
    public ResponseEntity<?> deleteDog(@PathVariable String dogId) {

        try {
            dogService.deleteDog(dogId);
            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("삭제 실패.");
        }
    }

//    @PatchMapping("/{dogId}")
//    public ResponseEntity<?> updateDog(@PathVariable DogModifyDto dto,)
//
}
