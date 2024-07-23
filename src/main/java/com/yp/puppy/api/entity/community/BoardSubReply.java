package com.yp.puppy.api.entity.community;

import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString(exclude = {"user", "likes"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "BoardSubReply")
public class BoardSubReply {
    @Id
//    @GenericGenerator(name = "uuid_generator", strategy = "uuid2") // uuid2 전략 적용!
//    @GeneratedValue(generator = "uuid_generator") // uuid_generator 사용
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "sub_reply_id")
    private String id;  // UUID는 String 타입을 사용


    private String content;  // 대댓글 내용
    private LocalDateTime createdAt = LocalDateTime.now();  // 작성 시간
    private LocalDateTime updatedAt;  // 수정시간
    private int isClean;  // 클린 여부 : (1) / 신고글 : (0)/검토중: (2)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private BoardReply boardReply;  // 댓글 번호  FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // 유저 FK. ⇒ 유저ID, 닉네임, 프로필

    @OneToMany(mappedBy = "boardSubReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Like> likes;
}
