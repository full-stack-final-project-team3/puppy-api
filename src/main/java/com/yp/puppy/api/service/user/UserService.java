package com.yp.puppy.api.service.user;


import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.dto.request.user.LoginRequestDto;
import com.yp.puppy.api.dto.request.user.UserSaveDto;
import com.yp.puppy.api.dto.response.user.LoginResponseDto;
import com.yp.puppy.api.entity.user.EmailVerification;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.user.EmailVerificationRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Value("${yp.mail.host}")
    private String mailHost;

    private final EmailVerificationRepository emailVerificationRepository;

    private final PasswordEncoder encoder;

    private final JavaMailSender mailSender;

    private final TokenProvider tokenProvider;




    /**                          이메일 중복확인 처리
     * @param - email로 유저가 존재하나 탐색
     * @return - false. 중복이지만 마무리 되지 않은 경우, 메일 재발송 후 false 리턴
     * @return - true. 중복이 아닐 경우
     */
    public boolean checkEmailDuplicate(String email) {
        boolean exists = userRepository.existsByEmail(email);
        log.info("Checking email {} is duplicated : {}", email, exists);

        // 중복인데 회원가입이 마무리되지 않은 회원은 중복이 아니라고 판단
        if (exists && notFinish(email)) {
            // 메일 재발송
            return false;
        }

        if (!exists) processSignUp(email);

        return exists;
    }


    /**                이메일을 통해 회원가입을 끝냈냐? 안끝냈냐 조회
     * @param email - 회원가입 유무 조회
     * @return - true는 인증코드 누락 || 비번 누락인 경우 코드 다시 생성, 보내주고 true리턴
     * @return - false는 회원가입을 끝낸 경우 false리턴
     */
    private boolean notFinish(String email) {
        // 이메일로 이사람이 회원가입을 끝냈냐? 안끝냈냐 조회
        User foundUser = userRepository.findByEmail(email).orElseThrow();
        if (!foundUser.isEmailVerified() || foundUser.getPassword() == null) {
            // 이메일 인증 누락이거나              비번이 누락된 경우
            EmailVerification ev = emailVerificationRepository.findByUser(foundUser).orElse(null);

            if (ev != null) {
                emailVerificationRepository.delete(ev);
            }

            generateAndSendCode(email,foundUser);
            return true;
        }
        return false;
    }



    private void generateAndSendCode(String email, User foundUser) {
        String code = sendVerificationEmail(email);

        // 인증코드 db 저장 로직
        EmailVerification verification = EmailVerification.builder()
                .verificationCode(code) // 인증코드
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .user(foundUser)
                .build();

        emailVerificationRepository.save(verification);
    }

    public String sendVerificationEmail(String email) {

        // 검증코드 생성
        String code = generateVerificationCode();

        // 이메일을 전송할 객체
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // 누구한테 이메일 보낼것?
            messageHelper.setTo(email);

            // 이메일 제목 설정
            messageHelper.setSubject("[Puppy verification email]");

            // 이메일 내용
            messageHelper.setText(
                    "인증 코드: <b style=\"font-weight: 700; letter-spacing: 5px; font-size: 30px;\">" + code + "</b>"
                    , true
            );

            // 전송자의 이메일 주소
            messageHelper.setFrom(mailHost);

            // 이메일 보내기
            mailSender.send(mimeMessage);
            log.info("{} 님에게 이메일 전송..", email);
            return code;

        }  catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 9000 + 1000));
    }



    public void processSignUp(String email) {

        // 1. 임시 회원가입
        User newUser = User.builder()
                .email(email)
                .build();
        User savedUser = userRepository.save(newUser);

        // 2. 이메일 인증 코드 발송
        generateAndSendCode(email, savedUser);
    }


    // 인증코드 체크
    public boolean isMatchCode(String email, String code) {

        // 이메일을 통해 회원정보 탐색
        User user = userRepository.findByEmail(email).orElse(null);

        // 유저가 있을 경우
        if (user != null) {
            // 인증코드를 받았었는지 탐색
            EmailVerification ev = emailVerificationRepository.findByUser(user).orElse(null);

            // 인증코드가 있고 만료시간이 지나지 않았고 코드번호가 일치할 경우 인증 성공
            if (
                   ev != null
                && ev.getExpiryDate().isAfter(LocalDateTime.now())
                && code.equals(ev.getVerificationCode())
            ) {
                user.setEmailVerified(true); // 변경
                userRepository.save(user); // update
                emailVerificationRepository.delete(ev); // 인증코드 삭제
                return true;
            } else { // 인증코드 틀렸거나, 만료된 경우
                emailVerificationRepository.delete(ev); // 기존 코드 삭제
                generateAndSendCode(email, user); // 재발송 & db저장
                return false;
            }
        }
        return false;
    }


    // 회원 인증 처리 (login)
    public LoginResponseDto authenticate(final LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("가입된 회원이 아닙니다.")
                );

        // 이메일 인증이 안되어있거나 패스워드를 설정하지 않은 회원
        if (!user.isEmailVerified() || user.getPassword() == null) {
            throw new RuntimeException("회원가입이 중단된 회원입니다");
        }

        // 패스워드 검증
        String inputPassword = dto.getPassword(); // 방금 입력값
        String encodedPassword = user.getPassword(); // db에 저장된 값

        if (!encoder.matches(inputPassword, encodedPassword)) { // 일치하지 않으면~
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        // 로그인 성공, 토큰 생성 섹션.
        // 인증정보(이메일, 닉네임, 프사, 토큰정보)를 클라이언트(프론트)에게 전송
        String token = tokenProvider.createToken(user);
        return LoginResponseDto.builder()
                .email(dto.getEmail())
                .role(user.getRole().toString())
                .token(token)
                .build();

    }




    // 회원가입 마무리 단계
    public void confirmSignUp(UserSaveDto dto) {

        // 기존 회원 정보 조회
        User user = userRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("회원 정보가 존재하지 않습니다.")
                );

        // DB 저장
        String password = dto.getPassword();
        String encodedPassword = encoder.encode(password);
        user.confirm(encodedPassword);
        userRepository.save(user);
    }


}
