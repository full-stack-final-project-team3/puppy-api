package com.yp.puppy.api.service.user;


import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.dto.request.user.LoginRequestDto;
import com.yp.puppy.api.dto.request.user.UserInfoModifyDto;
import com.yp.puppy.api.dto.request.user.UserSaveDto;
import com.yp.puppy.api.dto.response.user.LoginResponseDto;
import com.yp.puppy.api.dto.response.user.UserResponseDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.EmailVerification;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.exception.LoginFailException;
import com.yp.puppy.api.repository.user.DogRepository;
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
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final DogRepository dogRepository;

    @Value("${yp.mail.host}")
    private String mailHost;

    private final EmailVerificationRepository emailVerificationRepository;

    private final PasswordEncoder encoder;

    private final JavaMailSender mailSender;

    private final TokenProvider tokenProvider;


    /**
     * 이메일 중복확인 처리
     *
     * @param - email로 유저가 존재하나 탐색
     * @return - false. 중복이지만 마무리 되지 않은 경우, 메일 재발송 후 false 리턴
     * @return - true. 중복이 아닐 경우
     */
    public boolean checkEmailDuplicate(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일이 비어있습니다");
        }

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


    /**
     * 이메일을 통해 회원가입을 끝냈냐? 안끝냈냐 조회
     *
     * @param email - 회원가입 유무 조회
     * @return - true는 인증코드 누락 || 비번 누락인 경우 코드 다시 생성, 보내주고 true리턴
     * @return - false는 회원가입을 끝낸 경우 false리턴
     */
    private boolean notFinish(String email) {
        // 이메일로 이사람이 회원가입을 끝냈냐? 안끝냈냐 조회
        User foundUser = userRepository.findByEmail(email).orElse(null);
        if (foundUser == null) {
            // 유저가 존재하지 않으면 false 리턴
            return false;
        }

        if (!foundUser.isEmailVerified() || foundUser.getPassword() == null) {
            // 이메일 인증 누락이거나 비번이 누락된 경우
            EmailVerification ev = emailVerificationRepository.findByUser(foundUser).orElse(null);

            if (ev != null) {
                emailVerificationRepository.delete(ev);
            }

            generateAndSendCode(email, foundUser);
            return true;
        }
        return false;
    }


    private void generateAndSendCode(String email, User user) {
        // 기존 인증 코드 삭제
        emailVerificationRepository.findByUser(user).ifPresent(emailVerificationRepository::delete);

        // 새 인증 코드 생성
        String code = sendVerificationEmail(email);

        // 인증코드 db 저장 로직
        EmailVerification verification = EmailVerification.builder()
                .verificationCode(code) // 인증코드
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .user(user)
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

        } catch (MessagingException e) {
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
                        () -> new LoginFailException("가입된 회원이 아닙니다.")
                );

        // 이메일 인증이 안되어있거나 패스워드를 설정하지 않은 회원 (카카오 로그인 사용자는 예외)
        if (!"KAKAO".equals(user.getProvider()) && (!user.isEmailVerified() || user.getPassword() == null)) {
            throw new LoginFailException("회원가입이 중단된 회원입니다");
        }

        // 패스워드 검증 (카카오 로그인 사용자는 패스워드 검증 생략)
        if (!"KAKAO".equals(user.getProvider())) {  // KAKAO는 카카오 로그인 사용자의 provider
            String inputPassword = dto.getPassword(); // 방금 입력값
            String encodedPassword = user.getPassword(); // db에 저장된 값

            if (!encoder.matches(inputPassword, encodedPassword)) { // 일치하지 않으면~
                throw new LoginFailException("비밀번호가 틀렸습니다.");
            }
        }

        // 로그인 성공, 토큰 생성 섹션.
        // 인증정보(이메일, 닉네임, 프사, 토큰정보)를 클라이언트(프론트)에게 전송
        String token = tokenProvider.createToken(user);
//        log.debug("users nickname : {}, ", user.getNickname());
        return LoginResponseDto.builder()
                .email(dto.getEmail())
                .role(user.getRole().toString())
                .token(token)
                .userId(user.getId())
//                .nickname(user.getNickname())
                .build();
    }

    // 회원정보 수정을 통해 수정을 한 경우, 다시 setItem 해야함

    // 회원가입 마무리 단계 (카카오 로그인용)
    public User confirmSignUpKakao(UserSaveDto dto) {

        String password = dto.getPassword();
        String encodedPassword = encoder.encode(password);

        User user = User.builder()
                .nickname(dto.getNickname())
                .password(encodedPassword)
                .email(dto.getEmail())
                .provider("KAKAO") // 카카오 로그인 사용자의 provider 설정
                .build();

        return userRepository.save(user);
    }


    // 회원가입 마무리 단계
    public void confirmSignUp(UserSaveDto dto) {

        log.debug("UserSaveDto - {}", dto);

        // 기존 회원 정보 조회
        User user = userRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("회원 정보가 존재하지 않습니다.")
                );
//        log.info("user - {}", user);
        // DB 저장
        String password = dto.getPassword();
        String encodedPassword = encoder.encode(password);

        user.confirm(encodedPassword, dto.getNickname(), dto.getPhoneNumber());
//        user.setNickname(dto.getNickname());
        log.debug("saved user : {}", user);
        userRepository.save(user);
    }


    /**
     *          이메일로 유저를 찾아서 responseDto로 변환
     * @param email - 클라이언트에게 전송받은 이메일
     * @return - 렌더링용 dto로 변환
     */
    public UserResponseDto findUserByEmail(String email) {
        User foundUser = userRepository.findByEmail(email).orElseThrow();
        UserResponseDto dto = UserResponseDto.builder()
                .id(foundUser.getId())
                .email(foundUser.getEmail())
                .nickname(foundUser.getNickname())
                .role(foundUser.getRole())
                .birthday(foundUser.getBirthday())
                .point(foundUser.getPoint())
                .phoneNumber(foundUser.getPhoneNumber())
                .profileUrl(foundUser.getProfileUrl())
                .password(foundUser.getPassword())
                .hasDogInfo(foundUser.isHasDogInfo())
                .noticeCount(foundUser.getNoticeCount())
                .realName(foundUser.getRealName())
                .address(foundUser.getAddress())
                .warningCount(foundUser.getWarningCount())
                .dogList(foundUser.getDogList())
                .provider(foundUser.getProvider())
                .build();
        return dto;
    }


    public void modifyUserInfo(UserInfoModifyDto dto, String email) {
        User foundUser = userRepository.findByEmail(email).orElseThrow();
        List<Dog> foundList = dogRepository.findByUser(foundUser);


//        String encodedPassword = null;
//        if (dto.getPassword().length() > 20) {
//            encodedPassword = encoder.encode(dto.getPassword());
//        } else {
//            encodedPassword = dto.getPassword();
//        }
        String encodedPassword = null;
        if (dto.getPassword().length() > 1) { // 수정된 경우
            encodedPassword = encoder.encode(dto.getPassword());
            foundUser.setPassword(encodedPassword);
        }

        foundUser.setEmail(dto.getEmail());
        foundUser.setNickname(dto.getNickname());
        foundUser.setAddress(dto.getAddress());
        foundUser.setPhoneNumber(dto.getPhoneNumber());
        foundUser.setRealName(dto.getRealName());
        foundUser.setProfileUrl(dto.getProfileUrl());
        foundUser.setPoint(dto.getPoint());
        for (Dog dog : foundList) {
            foundUser.addDog(dog);
        }
        userRepository.save(foundUser);
    }

    public boolean checkIdentifier(String email) {

        // 있으면 true, 없으면 false
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * 닉네임 중복확인 처리
     *
     * @param - email 로 유저가 존재하나 탐색
     * @return - false. 중복이지만 마무리 되지 않은 경우, 메일 재발송 후 false 리턴
     * @return - true. 중복이 아닐 경우
     */
    public boolean checkNicknameDuplicate(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalArgumentException("닉네임이 비어있습니다");
        }

        boolean exists = userRepository.existsByNickname(nickname);
        log.info("Checking nickname {} is duplicated : {}", nickname, exists);

        return exists;
    }


    // 이메일 유무를 체크하고, 존재하면 이메일 발송 로직
    public boolean existByEmail(String email) {

        boolean exists = userRepository.existsByEmail(email);
        // 이메일 있으니까 db에서 회원인지 조회 -> 회원 아니면 false리턴
        // 회원이면 true 리턴하고 그 이메일로 유저 찾아서 코드까지 보내줌
        if (exists) {
            User foundUser = userRepository.findByEmail(email).orElseThrow();

            generateAndSendCode(email, foundUser);
            log.info("foundUser: {}", foundUser);
            return true;
        } else {
            return false;
        } // 응답 잘 옴! 이제 이거 가지고 프론트 작업
    }

    public boolean checkMatchCode(String email, String code) {
        // 이메일을 통해 회원정보 탐색
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            // 인증코드를 받았었는지 탐색
            EmailVerification ev = emailVerificationRepository.findByUser(user).orElse(null);

            // 인증코드가 있고 만료시간이 지나지 않았고 코드번호가 일치할 경우 인증 성공
            if (ev != null && ev.getExpiryDate().isAfter(LocalDateTime.now()) && code.equals(ev.getVerificationCode())) {
                emailVerificationRepository.delete(ev); // 인증코드 삭제
                return true;
            } else if (ev != null) { // 인증코드 틀렸거나, 만료된 경우
                emailVerificationRepository.delete(ev); // 기존 코드 삭제
                log.debug("인증코드 삭제! - {}", ev);
                generateAndSendCode(email, user); // 재발송 & db저장
            }
        }
        return false;
    }

    public void changePassword(String email, String password) {
        // 패스워드 인코딩
        String encodedPassword = encoder.encode(password);
        log.info("인코딩된 패스워드! - {}", encodedPassword);
        User foundUser = userRepository.findByEmail(email).orElseThrow();
        foundUser.setPassword(encodedPassword);

        userRepository.save(foundUser);

    }

    // 휴대폰 번호 중복 검증
    public boolean checkPhoneNumberDuplicate(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("휴대폰 번호가 비어있습니다");
        }

        boolean exists = userRepository.existsByPhoneNumber(phoneNumber);
        log.info("Checking phoneNumber {} is duplicated : {}", phoneNumber, exists);

        return exists;
    }

    public List<Board> getMyBoardList(String userId) {
        User foundUser = userRepository.findById(userId).orElseThrow();
        return foundUser.getBoard();
    }

    public boolean isDuplicatePassword(String password, String userId) {
        User foundUser = userRepository.findById(userId).orElseThrow();
        String originPassword = foundUser.getPassword();
        if (!encoder.matches(password, originPassword)) { // 일치하지 않으면?
            return false;
        } else {
            return true; // 일치할때 true
        }
    }

    public void deleteUser(String userId) {
        User foundUser = userRepository.findById(userId).orElseThrow();
        log.info("delete user info - {}", foundUser);
        userRepository.delete(foundUser);
    }
}
