package com.yp.puppy.api.service.dog;

import com.yp.puppy.api.dto.request.dog.DogSaveDto;
import com.yp.puppy.api.entity.user.Allergy;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.user.DogRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @Test
    @DisplayName("기범의 강아지 저장")
    void saveTest() {

        User kibeom = userRepository.findById("07686a36-75fc-4dc3-bd8c-69588857b1fa").orElseThrow();

        //given
        List<Allergy> allergyList = new ArrayList<>();
        Allergy chicken = Allergy.builder()
                .type(Allergy.AllergicType.CHICKEN)
                .build();
        allergyList.add(chicken);

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

        //when
        Dog dog = dogService.saveDog(dto);
        kibeom.addDog(dog);
        userRepository.save(kibeom);

        //then
        assertNotNull(dog);
//        assertEquals(Allergy.AllergicType.CHICKEN, dog.getAllergies().get(0).getType());
        System.out.println("\n\n\n\n\n\n\n");
        System.out.println("dog = " + dog);
        System.out.println("kibeom = " + kibeom);
    }
}