package com.yp.puppy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {
    private long id;
    private String boardTitle;
    private String boardContent;
    private String image;
    private LocalDateTime boardCreatedAt;
    private LocalDateTime boardUpdatedAt;
    private int viewCount;
    private int isClean;
    private UserDTO user;

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