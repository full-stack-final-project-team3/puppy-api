package com.yp.puppy.api.service.user;

import com.yp.puppy.api.dto.request.user.NoticeRequestDto;
import com.yp.puppy.api.dto.response.user.UserNoticeDto;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.entity.user.UserNotice;
import com.yp.puppy.api.repository.user.UserNoticeRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserNoticeService {

    private final UserNoticeRepository noticeRepository;
    private final UserRepository userRepository;


    public void addNotice(NoticeRequestDto dto) {
        log.info("addNotice {}", dto);
        User foundUser = userRepository.findById(dto.getUserId()).orElseThrow();
        UserNotice notice = UserNotice.builder()
                .message(dto.getMessage())
                .user(foundUser)
                .build();
        foundUser.setNoticeCount(foundUser.getNoticeCount()+1);
        foundUser.getUserNotices().add(notice);
        userRepository.save(foundUser);
    }

    public List<UserNoticeDto> findUserNotice(String userId) {
        User foundUser = userRepository.findById(userId).orElseThrow();
        log.info("users notices {}", foundUser.getUserNotices());
        return foundUser.getUserNotices().stream()
                .map(UserNoticeDto::new)
                .collect(Collectors.toList());
    }

    public User clickHandler(String noticeId, String userId) {
        User foundUser = userRepository.findById(userId).orElseThrow();
        foundUser.setNoticeCount(foundUser.getNoticeCount()-1);
        UserNotice notice = noticeRepository.findById(noticeId).orElseThrow();
        notice.setClicked(true);
        // 유저의 카운트 감소시켜야함
        noticeRepository.save(notice);
        userRepository.save(foundUser);
        return foundUser;
    }
}
