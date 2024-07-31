package com.yp.puppy.api.repository.user;

import com.yp.puppy.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {   

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPhoneNumber(String phoneNumber);
}
