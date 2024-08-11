package com.yp.puppy.api.dto.request.community;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.BoardImg;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPostDto {
    private String title;
    private String content;
    private List<BoardImg> imageUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginDate;
    private String userId;

    //엔터티로 변경하는 유틸 메서드
    public Board toEntity(){
        return Board.builder()
                .boardTitle(this.title)
                .boardContent(content)
                .images(imageUrl)
                .boardCreatedAt( LocalDateTime.now())
                .build();
    }
}
