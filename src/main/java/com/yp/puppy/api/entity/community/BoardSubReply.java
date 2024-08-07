package com.yp.puppy.api.entity.community;

import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString(exclude = {"user", "likes"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "BoardSubReply")
public class BoardSubReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_reply_id")
    private Long id;

    private String content;  // 대댓글 내용
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();  // 작성 시간
    @UpdateTimestamp
    private LocalDateTime updatedAt;  // 수정시간
    private int isClean;  // 클린 여부 : (1) / 신고글 : (0)/검토중: (2)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private BoardReply boardReply;  // 댓글 번호  FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // 유저 FK. ⇒ 유저ID, 닉네임, 프로필

    private String imageUrl;  // 이미지 URL

    @OneToMany(mappedBy = "boardSubReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();
}