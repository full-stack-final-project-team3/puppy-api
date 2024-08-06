package com.yp.puppy.api.dto.response.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private String id;
    private String reviewContent;
    private int rate;
    private LocalDateTime createdAt;
    private List<ReviewPicDto> reviewPics;
    private UserDTO user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewPicDto {
        private String id;
        private String reviewPic;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private String id;
        private String nickname;
        private String profileUrl;
        private String email;
    }
}
