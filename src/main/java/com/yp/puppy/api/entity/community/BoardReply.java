package com.yp.puppy.api.entity.community;


import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString(exclude = {"user", "board", "subReplies"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "BoardReply")
public class BoardReply {

    @Id
//    @GenericGenerator(strategy = "uuid2", name = "uuid_generator") //uuid2 전략 적용!
//    @GeneratedValue(generator = "uuid-generator")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "reply_id")
    private String id;  // 댓글 번호. PK

    //    @Column(name = "reply_content")
    private String replyContent;  // 댓글 내용
    //    @Column(name = "reply_created_at")
    private LocalDateTime replyCreatedAt = LocalDateTime.now();  // 댓글 작성일
    //    @Column(name = "reply_updated_at")
    private LocalDateTime replyUpdatedAt;  // 댓글 수정일
    //    @Column(name = "is_clean")
    private int isClean;  // 클린 여부 : (1) / 신고글 : (0)/검토중: (2)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;  // 게시글 번호  FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // 유저 FK. ⇒ 유저ID, 닉네임, 프로필

    @OneToMany(mappedBy = "boardReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardSubReply> subReplies;

    @OneToMany(mappedBy = "boardReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Like> likes;

}