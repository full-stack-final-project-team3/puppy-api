package com.yp.puppy.api.dto.response.user;

import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.entity.user.UserNotice;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNoticeDto {

    private String id;
    private String message;
    private String createdAt;  // 포맷된 문자열로 변경
    private boolean isClicked;

    // UserNotice를 받는 생성자
    public UserNoticeDto(UserNotice userNotice) {
        this.id = userNotice.getId();
        this.message = userNotice.getMessage();
        // LocalDateTime을 포맷된 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        this.createdAt = userNotice.getCreatedAt().format(formatter);
        this.isClicked = userNotice.isClicked();
    }
}