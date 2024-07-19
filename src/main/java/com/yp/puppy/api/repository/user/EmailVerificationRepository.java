package com.yp.puppy.api.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {

    // 유저 엔터티를 통째로 넣어서 인증코드 정보 탐색
    Optional<EmailVerification> findByUser(User user);

}
