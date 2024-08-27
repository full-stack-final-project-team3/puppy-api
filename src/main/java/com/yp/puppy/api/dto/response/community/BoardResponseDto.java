package com.yp.puppy.api.dto;

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
public class BoardResponseDto {
    private long id;
    private String boardTitle;
    private String boardContent;
    private List<String> images;
    private LocalDateTime boardCreatedAt;
    private LocalDateTime boardUpdatedAt;
    private int viewCount;
    private int isClean;
    private UserDTO user;
    private int replyCount; //댓글 카운트
    private long likeCount; //좋아요 카운트
    private KeywordDTO keyword;

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordDTO {
        private Long id;
        private String name;
    }

}