package com.yp.puppy.api.repository;

import com.yp.puppy.api.entity.EmailVerification;
import com.yp.puppy.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {


}
