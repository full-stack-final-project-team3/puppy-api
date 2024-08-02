package com.yp.puppy.api.controller.user;

import com.yp.puppy.api.dto.request.user.NoticeRequestDto;
import com.yp.puppy.api.dto.response.user.UserNoticeDto;
import com.yp.puppy.api.entity.user.UserNotice;
import com.yp.puppy.api.service.user.UserNoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@Slf4j
@RequiredArgsConstructor
public class UserNoticeController {

    private final UserNoticeService noticeService;


    @PostMapping("/add")
    public ResponseEntity<?> addUserNotice(@RequestBody NoticeRequestDto dto) {
        noticeService.addNotice(dto);
        return ResponseEntity.ok().body("ok");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserNotice(@PathVariable String userId) {
        List<UserNoticeDto> userNotice = noticeService.findUserNotice(userId);
        return ResponseEntity.ok().body(userNotice);
    }

    // 유저가 메시지를 클릭했을때 읽음처리.
    @PostMapping("/click/{noticeId}")
    public ResponseEntity<?> clickUserNotice(@PathVariable String noticeId) {
        log.info("noticeId: {}", noticeId);
        // clicked -> true, 알림 숫자 감소
        noticeService.clickHandler(noticeId);
        return ResponseEntity.ok().body("ok");
    }
}
