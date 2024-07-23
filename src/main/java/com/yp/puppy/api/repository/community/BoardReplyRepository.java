package com.yp.puppy.api.repository.community;

import com.yp.puppy.api.entity.community.BoardReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardReplyRepository extends JpaRepository<BoardReply,String> {
}
