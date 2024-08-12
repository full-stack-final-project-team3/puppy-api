package com.yp.puppy.api.service.community;

import com.yp.puppy.api.dto.response.community.BoardDetailResponseDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.BoardImg;
import com.yp.puppy.api.entity.community.BoardReply;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.community.BoardReplyRepository;
import com.yp.puppy.api.repository.community.BoardRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardReplyService {
    private final BoardReplyRepository boardReplyRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public BoardDetailResponseDto.ReplyDTO saveReply(Long boardId, String content, User user, MultipartFile image) {
        try {
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("Board not found"));
            User foundUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                imageUrl = saveImage(image);
            }

            BoardReply reply = BoardReply.builder()
                    .replyContent(content)
                    .board(board)
                    .user(foundUser)
                    .isClean(1)
                    .replyCreatedAt(LocalDateTime.now())
                    .build();

            if (imageUrl != null) {
                BoardImg boardImg = new BoardImg(imageUrl, null, reply, null);
                reply.setImage(boardImg);
            }

            BoardReply savedReply = boardReplyRepository.save(reply);
            return convertToReplyDTO(savedReply);
        } catch (Exception e) {
            log.error("Error saving reply: ", e);
            throw new RuntimeException("Failed to save reply", e);
        }
    }

    public BoardDetailResponseDto.ReplyDTO updateReply(Long replyId, String content, String userId) {
        log.debug("Updating reply. ReplyId: {}, Content: {}, UserId: {}", replyId, content, userId);

        BoardReply reply = boardReplyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found"));

        log.debug("Found reply: {}", reply);

        User replyUser = reply.getUser();
        log.debug("Reply user: {}", replyUser);

        UUID requestUserUUID = UUID.fromString(userId);
        log.debug("Request user UUID: {}", requestUserUUID);

        // 추가된 로그: UUID의 문자열 변환 비교
        log.debug("Reply user ID as String: {}, Request user UUID as String: {}", replyUser.getId().toString(), requestUserUUID.toString());

        // UUID 비교를 명시적으로 String으로 변환하여 비교
        if (!replyUser.getId().toString().equals(requestUserUUID.toString())) {
            log.error("Authorization failed. Reply user ID: {}, Request user ID: {}", replyUser.getId(), requestUserUUID);
            throw new IllegalArgumentException("You are not authorized to update this reply");
        }

        log.debug("Authorization successful");

        reply.setReplyContent(content);
        reply.setReplyUpdatedAt(LocalDateTime.now());
        BoardReply updatedReply = boardReplyRepository.save(reply);
        log.debug("Reply updated: {}", updatedReply);

        return convertToReplyDTO(updatedReply);
    }

    @Transactional
    public void deleteReply(Long replyId, String userId) {
        BoardReply reply = boardReplyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found"));

        // UUID 문자열을 UUID 객체로 변환
        UUID userUUID = UUID.fromString(userId);

        // toString()을 사용하여 UUID 비교
        if (!reply.getUser().getId().toString().equals(userUUID.toString())) {
            throw new IllegalArgumentException("You are not authorized to delete this reply");
        }

        boardReplyRepository.delete(reply);
    }

    public List<BoardDetailResponseDto.ReplyDTO> getRepliesByBoardId(Long boardId) {
        List<BoardReply> replies = boardReplyRepository.findByBoardId(boardId);
        return replies.stream()
                .map(this::convertToReplyDTO)
                .collect(Collectors.toList());
    }

    private BoardDetailResponseDto.ReplyDTO convertToReplyDTO(BoardReply reply) {
        log.debug("Converting reply to DTO: {}", reply);
        BoardDetailResponseDto.ReplyDTO dto = new BoardDetailResponseDto.ReplyDTO(
                reply.getId(),
                reply.getReplyContent(),
                reply.getReplyCreatedAt(),
                convertToUserDTO(reply.getUser()),
                reply.getImage() != null ? reply.getImage().getImgUrl() : null
        );
        log.debug("Converted DTO: {}", dto);
        return dto;
    }

    private BoardDetailResponseDto.UserDTO convertToUserDTO(User user) {
        log.debug("Converting user to DTO: {}", user);
        return new BoardDetailResponseDto.UserDTO(
                user.getId().toString(),
                user.getNickname(),
                user.getProfileUrl(),
                user.getEmail()
        );
    }

    private String saveImage(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir, fileName);

        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        return "/uploads/" + fileName;
    }
}
