package com.yp.puppy.api.service.dog;


import com.yp.puppy.api.dto.request.dog.DogSaveDto;
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

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class DogService {

    private final DogRepository dogRepository;
    private final UserRepository userRepository;

    private final EnumTranslator enumTranslator;


    public Dog saveDog(DogSaveDto dto, String userId) {

        User foundUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 낫 파운드!"));
        Dog dog = dto.toEntity(foundUser);
//        foundUser.addDog(dog);

//        userRepository.save(foundUser);
        dogRepository.save(dog);
        return dog;
    }

    public Dog findDog(String dogId) {
        Dog dog = dogRepository.findById(dogId).orElseThrow();
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
}
