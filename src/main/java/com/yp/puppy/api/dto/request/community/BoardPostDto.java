package com.yp.puppy.api.dto.request.community;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.user.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPostDto {
    private String title;
    private String content;
    private String imageUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginDate;
    private String userId;

    //엔터티로 변경하는 유틸 메서드
    public Board toEntity(){
        return Board.builder()
                .boardTitle(this.title)
                .boardContent(content)
                .image(imageUrl)
                .boardCreatedAt( LocalDateTime.now())
                .build();
    }
}
