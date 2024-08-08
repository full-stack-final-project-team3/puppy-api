package com.yp.puppy.api.dto.response.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDetailResponseDto {
    private long id;
    private String boardTitle;
    private String boardContent;
    private String image;
    private LocalDateTime boardCreatedAt;
    private LocalDateTime boardUpdatedAt;
    private int viewCount;
    private int isClean;
    private UserDTO user;
    private List<ReplyDTO> replies;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private String id;
        private String nickname;
        private String profileUrl;
        private String email;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyDTO {
        private Long id;
        private String replyContent;
        private LocalDateTime replyCreatedAt;
        private UserDTO user;
        private String imageUrl;
    }

//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class SubReplyDTO {
//        private Long id;
//        private String replyContent;
//        private LocalDateTime replyCreatedAt;
//        private LocalDateTime replyUpdatedAt;
//        private int isClean;
//        private UserDTO user;
//    }
}

