package com.yp.puppy.api.entity.community;


import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.bind.annotation.CrossOrigin;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString(exclude = {"user", "board", "subReplies"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "board_reply")
public class BoardReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;  // 댓글 번호. PK

    private String replyContent;  // 댓글 내용
    @CreationTimestamp
    private LocalDateTime replyCreatedAt = LocalDateTime.now();  // 댓글 작성일
    @UpdateTimestamp
    private LocalDateTime replyUpdatedAt;  // 댓글 수정일
    private int isClean;  // 클린 여부 : (1) / 신고글 : (0)/검토중: (2)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;  // 게시글 번호  FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // 유저 FK. ⇒ 유저ID, 닉네임, 프로필

    private String imageUrl;  // 이미지 URL

    @OneToMany(mappedBy = "boardReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardSubReply> subReplies= new ArrayList<>();

    @OneToMany(mappedBy = "boardReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();
}
