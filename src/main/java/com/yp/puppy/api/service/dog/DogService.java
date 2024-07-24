package com.yp.puppy.api.service.dog;


import com.yp.puppy.api.dto.request.dog.DogSaveDto;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.repository.user.DogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class DogService {

    private final DogRepository dogRepository;


    public Dog saveDog(DogSaveDto dto) {
        Dog dog = dto.toEntity();
        dogRepository.save(dog);
        return dog;
    }

    public Dog findDog(String dogId) {
        Dog dog = dogRepository.findById(dogId).orElseThrow();
        return dog;
    }
}
