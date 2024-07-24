package com.yp.puppy.api.service.dog;

import com.yp.puppy.api.dto.request.dog.DogSaveDto;
import com.yp.puppy.api.entity.user.Allergy;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.repository.user.DogRepository;
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

    @Test
    @DisplayName("강아지 저장")
    void saveTest() {
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
                .allergies(allergyList)
                .build();

        //when
        Dog dog = dogService.saveDog(dto);

        //then
        assertNotNull(dog);
        assertEquals("커피", dog.getDogName());
        assertEquals(Dog.Breed.POODLE, dog.getDogBreed());
        assertEquals(Dog.Sex.FEMALE, dog.getDogSex());
        assertFalse(dog.isNeutered());
        assertEquals(12.3, dog.getWeight());
        assertEquals(1, dog.getAllergies().size());
        assertEquals(Allergy.AllergicType.CHICKEN, dog.getAllergies().get(0).getType());
        System.out.println("dog = " + dog);
    }
}