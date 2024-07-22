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
    @GenericGenerator(name = "uuid_generator", strategy = "uuid2") // uuid2 전략 적용!
    @GeneratedValue(generator = "uuid_generator") // uuid_generator 사용
    @Column(name = "like_id")
    private String id;  // Like ID (PK)

//    @Column(name = "likable_id", nullable = false)
    private String likableId;  // 좋아요가 달린 대상의 ID (게시글, 댓글, 대댓글의 UUID)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 좋아요를 누른 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;  // 게시글 번호  FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="boardReplyId",nullable = false)
    private BoardSubReply boardReply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="boardSubReplyId",nullable = false)
    private BoardSubReply boardSubReply;


}
