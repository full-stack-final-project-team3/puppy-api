package com.yp.puppy.api.service.community;

import com.yp.puppy.api.dto.response.community.BoardDetailResponseDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.BoardImg;
import com.yp.puppy.api.entity.community.BoardReply;
import com.yp.puppy.api.entity.community.BoardSubReply;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.community.BoardImgRepository;
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
    private final BoardImgRepository boardImgRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
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
                    .subReplies(new ArrayList<>())
                    .build();

            if (imageUrl != null) {
                BoardImg boardImg = new BoardImg(imageUrl, board, reply, null);
                reply.setImage(boardImg);
            }

            BoardReply savedReply = boardReplyRepository.save(reply);
            return convertToReplyDTO(savedReply);
        } catch (Exception e) {
            log.error("Error saving reply: ", e);
            throw new RuntimeException("Failed to save reply", e);
        }
    }

    @Transactional
    public BoardDetailResponseDto.ReplyDTO updateReply(Long replyId, String content, String userId) {

        log.debug("🐶 댓글 수정 요청. 댓글 ID: {}, 내용: {}, 사용자 ID: {}", replyId, content, userId);

        log.debug("🐶 댓글 조회 시작");
        // 댓글 조회
        BoardReply reply = boardReplyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("🐶 댓글을 찾을 수 없습니다"));

        log.debug("🐶 댓글 쳌: {}",reply);
        // 권한 검사
        if (!reply.getUser().getId().toString().equals(userId)) {
            throw new IllegalArgumentException("🐶 이 댓글을 수정할 권한이 없습니다");
        }

        // 댓글 수정
        reply.setReplyContent(content);
        reply.setReplyUpdatedAt(LocalDateTime.now());

        // 댓글 저장
        BoardReply updatedReply = boardReplyRepository.save(reply);
        log.debug("Reply updated: {}", updatedReply);


        // DTO 변환
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
        log.debug("🐶 댓글을 DTO로 변환 시작: {}", reply);

        // 댓글의 이미지 URL 설정
        String replyImageUrl = null;
        if (reply.getImage() != null) {
            replyImageUrl = reply.getImage().getImgUrl();
            log.debug("🐶 댓글 이미지 URL (직접 참조): {}", replyImageUrl);
        } else {
            List<BoardImg> replyImages = boardImgRepository.findByBoardReplyId(reply.getId());
            // 댓글 이미지 URL 설정: board_id가 not null, reply_id가 not null, subreply_id가 null인 경우
            replyImageUrl = replyImages.stream()
                    .filter(img -> img.getBoard() != null && img.getBoardReply() != null && img.getBoardSubReply() == null)
                    .findFirst()
                    .map(BoardImg::getImgUrl)
                    .orElse(null);
            log.debug("🐶 댓글 이미지 URL (Repository 조회): {}", replyImageUrl);
        }

        // 서브 리플들을 DTO로 변환
        List<BoardDetailResponseDto.SubReplyDTO> subReplyDTOs = reply.getSubReplies().stream()
                .map(subReply -> {
                    // 대댓글의 이미지 URL 설정
                    String subReplyImageUrl = null;
                    if (subReply.getImage() != null) {
                        subReplyImageUrl = subReply.getImage().getImgUrl();
                        log.debug("🐶 대댓글 ID: {}, 대댓글 이미지 URL (직접 참조): {}", subReply.getId(), subReplyImageUrl);
                    } else {
                        List<BoardImg> subReplyImages = boardImgRepository.findByBoardSubReplyId(subReply.getId());
                        // 대댓글에 관련된 이미지 URL 가져오기: board_id, reply_id, subreply_id 모두 not null인 경우
                        subReplyImageUrl = subReplyImages.stream()
                                .filter(img -> img.getBoard() != null && img.getBoardReply() != null && img.getBoardSubReply() != null)
                                .findFirst()
                                .map(BoardImg::getImgUrl)
                                .orElse(null);
                        log.debug("🐶 대댓글 ID: {}, 대댓글 이미지 URL (Repository 조회): {}", subReply.getId(), subReplyImageUrl);
                    }

                    return new BoardDetailResponseDto.SubReplyDTO(
                            subReply.getId(),
                            subReply.getSubReplyContent(),
                            subReply.getSubReplyCreatedAt(),
                            convertToUserDTO(subReply.getUser()),
                            subReplyImageUrl
                    );
                })
                .collect(Collectors.toList());

        // ReplyDTO 생성
        BoardDetailResponseDto.ReplyDTO dto = new BoardDetailResponseDto.ReplyDTO(
                reply.getId(),
                reply.getReplyContent(),
                reply.getReplyCreatedAt(),
                convertToUserDTO(reply.getUser()),
                replyImageUrl,
                subReplyDTOs
        );

        log.debug("🐶 변환된 ReplyDTO: {}", dto);
        return dto;
    }

    @Transactional
    public BoardDetailResponseDto.SubReplyDTO saveSubReply(Long replyId, String content, User user, MultipartFile image) {
        BoardReply reply = boardReplyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found"));

        Board board = reply.getBoard();

        BoardSubReply subReply = BoardSubReply.builder()
                .subReplyContent(content)
                .boardReply(reply)
                .user(user)
                .isClean(1)
                .subReplyCreatedAt(LocalDateTime.now())
                .build();

        if (image != null && !image.isEmpty()) {
            // 이미지 저장
            String imageUrl = null;
            try {
                imageUrl = saveImage(image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            BoardImg boardImg = new BoardImg(imageUrl, board, reply, subReply);
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
