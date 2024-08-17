package com.yp.puppy.api.entity.community;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = {"board", "boardReply", "boardSubReply"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "board_img",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"board_id", "reply_id", "sub_reply_id", "img_url"})
        })
public class BoardImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_id")
    private Long id;

    @Column(name = "img_url", nullable = false)
    private String imgUrl;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @JsonIgnore
    private Board board;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private BoardReply boardReply;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_reply_id")
    private BoardSubReply boardSubReply;

    public BoardImg(String imgUrl, Board board, BoardReply boardReply, BoardSubReply boardSubReply) {
        this.imgUrl = imgUrl;
        this.board = board;
        this.boardReply = boardReply;
        this.boardSubReply = boardSubReply;
    }

    // 추가된 메소드
    public boolean isBoardImage() {
        return board != null && boardReply == null && boardSubReply == null;
    }

    public boolean isReplyImage() {
        return board != null && boardReply != null && boardSubReply == null;
    }

    public boolean isSubReplyImage() {
        return board != null && boardReply != null && boardSubReply != null;
    }
}
