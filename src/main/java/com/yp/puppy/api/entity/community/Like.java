package com.yp.puppy.api.entity.community;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import javax.persistence.*;

@Getter @Setter
@ToString(exclude = {"user", "boardSubReply", "board", "boardReply"})
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 좋아요를 누른 사용자

    @Column(name = "type", nullable = false)
    private String type;  // 좋아요의 유형 (예: board, reply, subReply)

    @Column(name = "type_id", nullable = false)  // 추가된 type_id 필드
    private Long typeId;  // 좋아요의 유형 ID

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id")
    private Board board;  // 게시글 번호 FK

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_reply_id")
    private BoardReply boardReply;  // 댓글 번호 FK

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_sub_reply_id")
    private BoardSubReply boardSubReply;  // 대댓글 번호 FK
}
