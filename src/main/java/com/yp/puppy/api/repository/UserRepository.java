package com.yp.puppy.api.repository;

import com.yp.puppy.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
