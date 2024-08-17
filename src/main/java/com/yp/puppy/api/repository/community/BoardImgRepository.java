package com.yp.puppy.api.repository.community;

import com.yp.puppy.api.entity.community.BoardImg;
import com.yp.puppy.api.entity.community.BoardReply;
import com.yp.puppy.api.entity.community.BoardSubReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardImgRepository extends JpaRepository<BoardImg, Long> {
    // BoardReply 객체를 통해 이미지를 찾는 메소드
    List<BoardImg> findByBoardReplyId(Long replyId);
    List<BoardImg> findByBoardSubReplyId(Long subReplyId);
}
