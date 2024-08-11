package com.yp.puppy.api.entity.community;

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
@Table(name = "board_img")
public class BoardImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_id")
    private Long id;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private BoardReply boardReply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_reply_id")
    private BoardSubReply boardSubReply;

    public BoardImg(String imgUrl, Board board, BoardReply boardReply, BoardSubReply boardSubReply) {
        this.imgUrl = imgUrl;
        this.board = board;
        this.boardReply = boardReply;
        this.boardSubReply = boardSubReply;
    }
}