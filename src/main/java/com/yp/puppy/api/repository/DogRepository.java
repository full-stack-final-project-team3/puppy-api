package com.yp.puppy.api.repository;

import com.yp.puppy.api.entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DogRepository extends JpaRepository<Dog, String> {

}
