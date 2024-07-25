package com.yp.puppy.api.service.dog;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yp.puppy.api.dto.request.dog.DogSaveDto;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.user.DogRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class DogServiceTest {


    @Autowired
    private DogService dogService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DogRepository dogRepository;

    @BeforeEach
    void setUp() {
        String email = "hgb926@naver.com";
        User kibeom = userRepository.findByEmail(email).orElseThrow();

        //given


        DogSaveDto dto = DogSaveDto.builder()
                .dogName("커피")
                .dogBreed(Dog.Breed.POODLE)
                .dogSex(Dog.Sex.FEMALE)
                .isNeutered(false)
                .weight(12.3)
                .birthday(LocalDate.now())
                .user(kibeom)
//                .allergies(allergyList)
                .build();

        DogSaveDto dto2 = DogSaveDto.builder()
                .dogName("땅콩")
                .dogBreed(Dog.Breed.SHIH_TZU)
                .dogSex(Dog.Sex.FEMALE)
                .isNeutered(true)
                .weight(7.3)
                .birthday(LocalDate.now().minusYears(5))
                .user(kibeom)
//                .allergies(allergyList)
                .build();

        //when
        Dog dog = dogService.saveDog(dto);
        Dog dog2 = dogService.saveDog(dto2);
        kibeom.addDog(dog);
        kibeom.addDog(dog2);
        userRepository.save(kibeom);

    }


    @Test
    @DisplayName("기범의 강아지 저장")
    void saveTest() {

        //given
        String email = "hgb926@naver.com";

        User kibeom = userRepository.findByEmail(email).orElseThrow();


        DogSaveDto dto = DogSaveDto.builder()
                .dogName("커피")
                .dogBreed(Dog.Breed.POODLE)
                .dogSex(Dog.Sex.FEMALE)
                .isNeutered(false)
                .weight(12.3)
                .birthday(LocalDate.now())
                .user(kibeom)
//                .allergies(allergyList)
                .build();

        DogSaveDto dto2 = DogSaveDto.builder()
                .dogName("땅콩")
                .dogBreed(Dog.Breed.SHIH_TZU)
                .dogSex(Dog.Sex.FEMALE)
                .isNeutered(true)
                .weight(7.3)
                .birthday(LocalDate.now().minusYears(5))
                .user(kibeom)
//                .allergies(allergyList)
                .build();

        //when
        Dog dog = dogService.saveDog(dto);
        Dog dog2 = dogService.saveDog(dto2);
        kibeom.addDog(dog);
        kibeom.addDog(dog2);
        userRepository.save(kibeom);

        //then
        assertNotNull(dog);
//        assertEquals(Allergy.AllergicType.CHICKEN, dog.getAllergies().get(0).getType());
        System.out.println("\n\n\n\n\n\n\n");
        System.out.println("dog = " + dog);
        System.out.println("kibeom = " + kibeom);
    }



    @Test
    @DisplayName("kibeom의 강아지 조회")
    void findOne() {
        //given
        String email = "hgb926@naver.com";
        User foundUser = userRepository.findByEmail(email).orElseThrow();
        //when

        List<Dog> foundDog = dogRepository.findByUser(foundUser);

        //then
        System.out.println("foundDog = " + foundDog); // 1건이건 2건이건 잘 찾아온다.
    }

    @Test
    @DisplayName("kibeom의 강아지 삭제")
    void deleteTest() {
        //given
        String email = "hgb926@naver.com";
        // 삭제할 강아지
        String dogName = "땅콩";
        User foundUser = userRepository.findByEmail(email).orElseThrow();

        //when
        List<Dog> foundDogs = dogRepository.findByUser(foundUser);

        // Iterator를 사용하여 안전하게 요소를 제거
        Iterator<Dog> iterator = foundDogs.iterator();
        while (iterator.hasNext()) {
            Dog dog = iterator.next();
            if (dog.getDogName().equals(dogName)) {
                iterator.remove();
                dogRepository.delete(dog);
            }
        }

        //then
        assertEquals(foundDogs.size(), 1);
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\nfoundDogs = " + foundDogs);
    }




}