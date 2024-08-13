package com.yp.puppy.api.service.community;

import com.yp.puppy.api.dto.response.community.BoardDetailResponseDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.BoardImg;
import com.yp.puppy.api.entity.community.BoardReply;
import com.yp.puppy.api.entity.community.BoardSubReply;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.community.BoardReplyRepository;
import com.yp.puppy.api.repository.community.BoardRepository;
import com.yp.puppy.api.repository.community.BoardSubReplyRepository;
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
import java.util.ArrayList;
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
    private final BoardSubReplyRepository boardSubReplyRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public BoardDetailResponseDto.ReplyDTO saveReply(Long boardId, String content, User user, MultipartFile image) {
        try {
            // Board 엔티티 조회
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("Board not found"));

            // User 엔티티 조회
            User foundUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            // 이미지 저장
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                imageUrl = saveImage(image);
            }

            // BoardReply 엔티티 생성
            BoardReply reply = BoardReply.builder()
                    .replyContent(content)
                    .board(board)
                    .user(foundUser)
                    .isClean(1)
                    .replyCreatedAt(LocalDateTime.now())
                    .subReplies(new ArrayList<>())
                    .build();

            // 이미지가 있는 경우 BoardImg 엔티티 생성
            if (imageUrl != null) {
                BoardImg boardImg = new BoardImg(imageUrl, null, reply, null);
                reply.setImage(boardImg);
            }

            // 댓글 저장
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

        // UUID 비교
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

        UUID userUUID = UUID.fromString(userId);

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

        // 서브 리플들을 DTO로 변환
        List<BoardDetailResponseDto.SubReplyDTO> subReplyDTOs = reply.getSubReplies().stream()
                .map(subReply -> new BoardDetailResponseDto.SubReplyDTO(
                        subReply.getId(),
                        subReply.getSubReplyContent(),
                        subReply.getSubReplyCreatedAt(),
                        convertToUserDTO(subReply.getUser()),
                        subReply.getImage() != null ? subReply.getImage().getImgUrl() : null
                ))
                .collect(Collectors.toList());

        // ReplyDTO 생성
        BoardDetailResponseDto.ReplyDTO dto = new BoardDetailResponseDto.ReplyDTO(
                reply.getId(),
                reply.getReplyContent(),
                reply.getReplyCreatedAt(),
                convertToUserDTO(reply.getUser()),
                reply.getImage() != null ? reply.getImage().getImgUrl() : null,
                subReplyDTOs
        );

        log.debug("Converted DTO: {}", dto);
        return dto;
    }

    @Transactional
    public BoardDetailResponseDto.SubReplyDTO saveSubReply(Long replyId, String content, User user, MultipartFile image) {
        BoardReply reply = boardReplyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found"));

        BoardSubReply subReply = BoardSubReply.builder()
                .subReplyContent(content)
                .boardReply(reply)
                .user(user)
                .isClean(1)
                .build();

        if (image != null && !image.isEmpty()) {
            // 이미지 저장
            String imageUrl = null;
            try {
                imageUrl = saveImage(image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            BoardImg boardImg = new BoardImg(imageUrl, null, null, subReply);
            subReply.setImage(boardImg);
        }

        BoardSubReply savedSubReply = boardSubReplyRepository.save(subReply);
        return convertToSubReplyDTO(savedSubReply);
    }

    @Transactional
    public BoardDetailResponseDto.SubReplyDTO updateSubReply(Long subReplyId, String content, String userId) {
        BoardSubReply subReply = boardSubReplyRepository.findById(subReplyId)
                .orElseThrow(() -> new EntityNotFoundException("SubReply not found"));

        if (!subReply.getUser().getId().toString().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to update this sub-reply");
        }

        subReply.setSubReplyContent(content);
        BoardSubReply updatedSubReply = boardSubReplyRepository.save(subReply);
        return convertToSubReplyDTO(updatedSubReply);
    }

    @Transactional
    public void deleteSubReply(Long subReplyId, String userId) {
        BoardSubReply subReply = boardSubReplyRepository.findById(subReplyId)
                .orElseThrow(() -> new EntityNotFoundException("SubReply not found"));

        if (!subReply.getUser().getId().toString().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this sub-reply");
        }

        boardSubReplyRepository.delete(subReply);
    }

    private BoardDetailResponseDto.SubReplyDTO convertToSubReplyDTO(BoardSubReply subReply) {
        return new BoardDetailResponseDto.SubReplyDTO(
                subReply.getId(),
                subReply.getSubReplyContent(),
                subReply.getSubReplyCreatedAt(),
                convertToUserDTO(subReply.getUser()),
                subReply.getImage() != null ? subReply.getImage().getImgUrl() : null
        );
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
