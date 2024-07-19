package com.yp.puppy.api.repository.user;

import com.yp.puppy.api.entity.user.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DogRepository extends JpaRepository<Dog, String> {

}
