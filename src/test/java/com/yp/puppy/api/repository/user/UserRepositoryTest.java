package com.yp.puppy.api.repository.user;

import com.yp.puppy.api.entity.user.Allergy;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.Role;
import com.yp.puppy.api.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DogRepository dogRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("hgb@naver.com")
                .password("gksrlqja1!")
                .role(Role.USER)
                .emailVerified(true)
                .autoLogin(false)
                .point(5000)
                .birthday(LocalDate.now().minusYears(27))
                .nickname("기범")
                .phoneNumber("01094401851")
                .build();

        Dog dog1 = Dog.builder()
                .dogName("콩순이")
                .birthday(LocalDate.now().minusYears(3))
                .dogBreed(Dog.Breed.POODLE)
                .weight(8.3)
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

        Allergy allergy1 = Allergy.builder()
                .type(Allergy.AllergicType.CORN)
                .build();

        Allergy allergy2 = Allergy.builder()
                .type(Allergy.AllergicType.BEEF)
                .build();

        Allergy allergy3 = Allergy.builder()
                .type(Allergy.AllergicType.PORK)
                .build();

        Allergy allergy4 = Allergy.builder()
                .type(Allergy.AllergicType.CHICKEN)
                .build();

        dog1.addAllergy(allergy1);
        dog1.addAllergy(allergy2);
        dog2.addAllergy(allergy3);
        dog2.addAllergy(allergy4);



        user.addDog(dog1);
        user.addDog(dog2);

        userRepository.save(user);
    }

    @Test
    @DisplayName("저장 테스트")
    void saveTest() {
        //given
        User foundUser = userRepository.findByEmail("hgb@naver.com").orElseThrow();
        List<Dog> dogList = foundUser.getDogList();

        //when
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.println("foundUser = " + foundUser);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.println("dogList = " + dogList);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        //then
        assertEquals(2, dogList.size());
    }
}