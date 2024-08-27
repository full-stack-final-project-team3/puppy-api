package com.yp.puppy.api.entity.community;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"boardReply", "subReplies", "likes"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "board_sub_reply")
public class BoardSubReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_reply_id")
    private Long id;

    private String subReplyContent;
    @CreationTimestamp
    private LocalDateTime subReplyCreatedAt = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime subReplyUpdatedAt;
    private int isClean;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private BoardReply boardReply;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "boardSubReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BoardImg image;

    @OneToMany(mappedBy = "boardSubReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();
}