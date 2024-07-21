package com.yp.puppy.api.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yp.puppy.api.entity.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.yp.puppy.api.entity.user.Allergy.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class UserRepositoryTest {

    @Autowired
    private JPAQueryFactory factory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private AllergyRepository allergyRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("hgb9266@naver.com")
                .password("gksrlqja1!")
                .role(Role.USER)
                .emailVerified(true)
                .autoLogin(false)
                .point(5000)
                .birthday(LocalDate.now().minusYears(27))
                .nickname("기범")
                .phoneNumber("01094401851")
                .build();

        User user2 = User.builder()
                .email("jihoon@naver.com")
                .password("jihoon1!")
                .role(Role.USER)
                .emailVerified(true)
                .autoLogin(false)
                .point(15000)
                .birthday(LocalDate.now().minusYears(26))
                .nickname("지훈")
                .phoneNumber("01012341234")
                .build();

        List<Allergy> allergyList = new ArrayList<>();


        Dog dog1 = Dog.builder()
                .dogName("콩순이")
                .birthday(LocalDate.now().minusYears(3))
                .dogBreed(Dog.Breed.POODLE)
                .weight(8.3)
                .allergies(allergyList)
                .isNeutered(false)
                .dogSex(Dog.Sex.FEMALE)
                .build();




        Dog dog2 = Dog.builder()
                .dogName("춘식이")
                .birthday(LocalDate.now().minusYears(7))
                .dogBreed(Dog.Breed.POMERANIAN)
                .weight(5.3)
                .isNeutered(false)
                .dogSex(Dog.Sex.MALE)
                .build();

        Dog dog3 = Dog.builder()
                .dogName("지훈의 개")
                .birthday(LocalDate.now().minusYears(7))
                .dogBreed(Dog.Breed.POMERANIAN)
                .weight(5.3)
                .isNeutered(false)
                .dogSex(Dog.Sex.MALE)
                .build();

        user.addDog(dog1);
        user.addDog(dog2);
        user2.addDog(dog3);

        userRepository.save(user);
        userRepository.save(user2);


    }

    @Test
    @DisplayName("저장 테스트")
    void saveTest() {
        //given
        User foundUser = userRepository.findByEmail("hgb926@naver.com").orElseThrow();
        List<Dog> dogList = foundUser.getDogList();

        //when
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.println("foundUser = " + foundUser);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.println("dogList = " + dogList);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");

        for (Dog dog : dogList) {
            System.out.println(dog.getDogName() + "의 알러지 목록:");
            for (Allergy allergy : dog.getAllergies()) {
                System.out.println(allergy.getType());
            }
        }
        //then
        assertEquals(2, dogList.size());
        assertEquals(2, dogList.get(0).getAllergies().size()); // 첫 번째 강아지의 알러지 개수 확인
        assertEquals(2, dogList.get(1).getAllergies().size()); // 두 번째 강아지의 알러지 개수 확인
    }

    @Test
    @DisplayName("강아지 삭제 테스트")
    void deleteDogTest() {
        //given
        User foundUser = userRepository.findByEmail("hgb9266@naver.com").orElseThrow();
        String dogName = "춘식이";
        Dog dogToDelete = foundUser.findDogByName(dogName); // 춘식이를 선택
        System.out.println("dogToDelete = " + dogToDelete);

        //when
        foundUser.getDogList().remove(dogToDelete);
        dogRepository.delete(dogToDelete);
        userRepository.save(foundUser);

        //then
        User updatedUser = userRepository.findByEmail("hgb9266@naver.com").orElseThrow();
        List<Dog> updatedDogList = updatedUser.getDogList();

        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.println("updatedUser = " + updatedUser);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.println("updatedDogList = " + updatedDogList);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");

    }



    @Test
    @DisplayName("내 강아지 정보 변경")
    void updateTest() {
        //given
        String prevDogName = "춘식이";
        String newDogName = "쿵쾅이";
        //when
        User foundUser = userRepository.findByEmail("hgb9266@naver.com").orElseThrow();
        Dog dogByName = foundUser.findDogByName(prevDogName);
        System.out.println("\n\n\n\ndogByName = " + dogByName);
        dogByName.setDogName(newDogName);
        dogRepository.save(dogByName);
        userRepository.save(foundUser);
        //then
        System.out.println("\n\n\n\nfoundUser = " + foundUser);
        System.out.println("\n\n\n\nnewDog = " + dogByName);
    }



    @Test
    @DisplayName("내 강아지의 알러지 변경")
    void 알러지변경() {
        //given
        String dogName = "춘식이";
        AllergicType newAllergy = AllergicType.WHEAT;
        //when

        //then
    }


}