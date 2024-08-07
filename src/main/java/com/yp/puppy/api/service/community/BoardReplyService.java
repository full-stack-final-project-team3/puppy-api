package com.yp.puppy.api.service.community;

import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.BoardReply;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.community.BoardReplyRepository;
import com.yp.puppy.api.repository.community.BoardRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BoardReplyService {
    private final BoardReplyRepository boardReplyRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public BoardReply saveReply(Long boardId, String content, User user, MultipartFile image) throws IOException {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
        User foundUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = uploadImage(image);
        }

        BoardReply reply = BoardReply.builder()
                .replyContent(content)
                .board(board)
                .user(foundUser)
                .imageUrl(imageUrl)
                .isClean(1)
                .build();

        return boardReplyRepository.save(reply);
    }

    private String uploadImage(MultipartFile image) throws IOException {
        // 이미지 업로드 로직 구현
        // 실제 구현에서는 파일 시스템이나 클라우드 스토리지에 이미지를 저장하고 URL을 반환해야 합니다.
        return "http://example.com/images/" + image.getOriginalFilename();
    }
}