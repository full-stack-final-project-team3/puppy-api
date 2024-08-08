package com.yp.puppy.api.entity.community;

import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString(exclude = {"user","boardSubReply","board"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "Likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY 전략 사용
    @Column(name = "like_id")
    private Long id;  // Like ID (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 좋아요를 누른 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;  // 게시글 번호  FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="board_reply_id")
    private BoardReply boardReply;  // 댓글 번호 FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="board_sub_reply_id")
    private BoardSubReply boardSubReply;  // 대댓글 번호 FK

}
