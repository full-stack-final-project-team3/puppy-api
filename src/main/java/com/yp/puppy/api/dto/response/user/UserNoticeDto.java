package com.yp.puppy.api.dto.response.user;

import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.entity.user.UserNotice;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNoticeDto {

    private String id;
    private String message;
    private LocalDateTime createdAt;
    private boolean isClicked;
//    private User user;

    // UserNotice를 받는 생성자
    public UserNoticeDto(UserNotice userNotice) {
        this.id = userNotice.getId();
        this.message = userNotice.getMessage();
        this.createdAt = userNotice.getCreatedAt();
        this.isClicked = userNotice.isClicked();
//        this.user = userNotice.getUser();
    }
}