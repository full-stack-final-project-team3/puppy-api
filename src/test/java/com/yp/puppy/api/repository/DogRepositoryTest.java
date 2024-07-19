package com.yp.puppy.api.repository;

import com.yp.puppy.api.entity.Dog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Role;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class DogRepositoryTest {

    @Autowired
    private DogRepository dogRepository;

    @BeforeEach
    void setUp() {
        Dog dog = Dog.builder()
                .id("1")
                .dogName("춘식")
                .build();
        dogRepository.save(dog);
    }
    
    
//    @Test
//    @DisplayName("저장")
//    void () {
        //given
        
        //when
        
        //then
//    }
    

}