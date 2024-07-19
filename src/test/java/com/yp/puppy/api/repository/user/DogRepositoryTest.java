package com.yp.puppy.api.repository.user;

import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.Role;
import com.yp.puppy.api.entity.user.User;
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
class DogRepositoryTest {

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("유저1이 강아지 1 저장")
    void saveT() {
        //given
        Dog dog1 = Dog.builder()
                .dogName("달팽이의 강아지!")
                .birthday(LocalDate.now())
                .build();

        dogRepository.save(dog1);

        List<Dog> list = dogRepository.findAll();
        
        User user = User.builder()
                .email("dog@yp.com")
                .password("123456")
                .role(Role.USER)
                .emailVerified(true)
                .nickname("달팽이")
                .dogList(list)
                .build();

        userRepository.save(user);
        User foundUser = userRepository.findById(user.getId()).orElseThrow();
        
        //when
        
        System.out.println("\n\n\n\n\n");
//        System.out.println("foundUser = " + foundUser);
        System.out.println("dog1 = " + Math.abs((int) (dog1.getBirthday().getYear() - 2024)) );
        System.out.println("\n\n\n\n\n");

        //then
    }



}