package com.yp.puppy.api.entity.community;

import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@ToString(exclude = {"user","replies"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "Board")
public class Board {
    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2") // uuid2 전략 적용
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "board_id")
    private String id;

//    @Column(name = "board_title")
    private String boardTitle;  // 게시판 제목
//    @Column(name = "board_content")
    private String boardContent;  // 게시판 내용
//    @Column(name = "board_created_at")
    private LocalDateTime boardCreatedAt = LocalDateTime.now();  // 게시글 작성일
//    @Column(name = "board_updated_at")
    private LocalDateTime boardUpdatedAt;  // 게시글 수정시간
//    @Column(name = "view_count")
    private int viewCount;  // 조회수
//    @Column(name = "is_clean")
    private int isClean;  // 클린 여부 : (1) / 신고글 : (0)/검토중: (2)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;  // 유저 FK. ⇒ 유저ID, 닉네임, 프로필

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardReply> replies;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Like> likes;
}
