package com.yp.puppy.api.service;


import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Value("${study.mail.host}")
    private String mailHost;

    private final PasswordEncoder encoder;

    private final JavaMailSender mailSender;

    private final TokenProvider tokenProvider;

}
