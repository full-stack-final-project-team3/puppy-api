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
@ToString(exclude = {"user", "board", "subReplies", "likes"})
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
    private Long id;

    private String replyContent;
    @CreationTimestamp
    private LocalDateTime replyCreatedAt = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime replyUpdatedAt;
    private int isClean;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "boardReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BoardImg image;

    @JsonIgnore
    @OneToMany(mappedBy = "boardReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardSubReply> subReplies = new ArrayList<>();

    @OneToMany(mappedBy = "boardReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();
}