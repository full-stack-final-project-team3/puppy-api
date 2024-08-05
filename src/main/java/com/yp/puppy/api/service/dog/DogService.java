package com.yp.puppy.api.service.dog;


import com.yp.puppy.api.dto.request.dog.DogModifyDto;
import com.yp.puppy.api.dto.request.dog.DogSaveDto;
import com.yp.puppy.api.dto.response.dog.DogResponseDto;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.user.DogRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import com.yp.puppy.api.util.EnumTranslator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class DogService {

    private final DogRepository dogRepository;
    private final UserRepository userRepository;



    public Dog saveDog(DogSaveDto dto, String email) {

        User foundUser = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("유저 낫 파운드!"));
        Dog dog = dto.toEntity(foundUser);
        dogRepository.save(dog);
        return dog;
    }

    public Dog findDog(String dogId) {
        Dog dog = dogRepository.findById(dogId).orElseThrow();
//        DogResponseDto dto = null;
//        DogResponseDto entity = dto.toEntity(dog);
//        log.info("DogResponseDto: {}", entity);

        return dog;
    }

    public void deleteDog(String dogId) {
        dogRepository.deleteById(dogId);
    }

    public List<Dog> findMyPuppies(String userId) {
        User foundUser = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        List<Dog> foundList = dogRepository.findByUser(foundUser);

        return foundList;
    }

    public void deleteAllergy(String dogId, Dog.Allergy allergy) {
        // 개체를 데이터베이스에서 찾음
        Dog foundDog = dogRepository.findById(dogId).orElseThrow();

        // 알레르기 리스트를 가져와서 수정
        List<Dog.Allergy> allergies = foundDog.getAllergies();
        if (allergies != null && allergies.contains(allergy)) {
            allergies.remove(allergy);
            foundDog.setAllergies(allergies);
            // 변경된 개체를 저장소에 다시 저장
            dogRepository.save(foundDog);
        } else {
            // 알레르기 리스트가 null이거나 알레르기가 리스트에 없는 경우 처리
            throw new IllegalArgumentException("Allergy not found in dog's allergy list.");
        }
    }

    /**
     *  마이페이지에서 수정하는 요청을 받는 dto (알러지 제외)
     * @param dto - 마이페이지에서의 입력값
     */
    public void modifyInMyPage(String dogId, DogModifyDto dto) {
        Dog foundDog = dogRepository.findById(dogId).orElseThrow();
        foundDog.setWeight(dto.getWeight());
        foundDog.setDogProfileUrl(dto.getDogProfileUrl());
        log.info("dto's profile url: {}", dto.getDogProfileUrl());
        dogRepository.save(foundDog);
    }

    // 알러지 수정 로직
    public Dog postAllergy(String dogId, List<Dog.Allergy> allergy) {
        Dog foundDog = dogRepository.findById(dogId).orElseThrow();
        log.info("allergies : {}", allergy);
        foundDog.setAllergies(allergy);
        dogRepository.save(foundDog);
        return foundDog;
    }
}
