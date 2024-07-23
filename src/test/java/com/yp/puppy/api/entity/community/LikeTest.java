package com.yp.puppy.api.entity.community;

import com.yp.puppy.api.repository.community.BoardReplyRepository;
import com.yp.puppy.api.repository.community.BoardRepository;
import com.yp.puppy.api.repository.community.BoardSubReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@Rollback
class LikeTest {
  @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardReplyRepository boardReplyRepository;
    @Autowired
    BoardSubReplyRepository boardSubReplyRepository;
}