package com.yp.puppy.api.repository.user;

import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, String> {

    Optional<Dog> findByUser(User user);
}
