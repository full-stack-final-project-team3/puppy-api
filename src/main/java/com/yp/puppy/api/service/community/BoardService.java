package com.yp.puppy.api.service.community;

import com.yp.puppy.api.dto.BoardResponseDto;
import com.yp.puppy.api.dto.request.community.BoardSaveDto;
import com.yp.puppy.api.dto.response.community.BoardDetailResponseDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.BoardImg;
import com.yp.puppy.api.entity.user.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<BoardResponseDto> getBoards(String sort) {
        List<Board> boards = boardRepository.findBoards(sort);
        return boards.stream()
                .map(this::convertToBoardResponseDto)
                .collect(Collectors.toList());
    }

    // 게시글 저장 메서드
    public BoardResponseDto saveBoard(BoardSaveDto dto, List<MultipartFile> files) {
        try {
            User user = userRepository.findById(dto.getUser().getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            Board board = Board.builder()
                    .boardTitle(dto.getBoardTitle())
                    .boardContent(dto.getBoardContent())
                    .user(user)
                    .boardCreatedAt(LocalDateTime.now())
                    .images(new ArrayList<>())
                    .build();

            Board savedBoard = boardRepository.save(board);

            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    BoardImg image = saveImage(file, savedBoard);
                    savedBoard.getImages().add(image);
                }
                savedBoard = boardRepository.save(savedBoard);
            }

            return convertToBoardResponseDto(savedBoard);
        } catch (Exception e) {
            log.error("Error saving board: ", e);
            throw new RuntimeException("Failed to save board", e);
        }
    }

    // 이미지 파일 저장 및 URL 생성 메서드
    private BoardImg saveImage(MultipartFile file, Board board) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);

            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            // 이미지 파일의 URL 생성
            String imgUrl = "/uploads/" + fileName;

            return BoardImg.builder()
                    .imgUrl(imgUrl)
                    .board(board)
                    .build();
        } catch (IOException e) {
            log.error("Error saving image: ", e);
            throw new RuntimeException("Failed to save image", e);
        }
    }

    public BoardResponseDto getBoardById(long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));

        return convertToBoardResponseDto(board);
    }

    public BoardDetailResponseDto getBoardDetailById(long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));

        return convertToBoardDetailResponseDto(board);
    }

    public void deleteBoard(Long boardId, String userId) {
        log.info("Attempting to delete board with id: {} by user: {}", boardId, userId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardId));

        if (!board.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You don't have permission to delete this board");
        }

        boardRepository.delete(board);
        log.info("Board with id: {} deleted successfully", boardId);
    }

    public BoardResponseDto convertToBoardResponseDto(Board board) {
        List<String> imageUrls = new ArrayList<>();
        if (board.getImages() != null) {
            imageUrls = board.getImages().stream()
                    .map(BoardImg::getImgUrl)
                    .collect(Collectors.toList());
        }

        return new BoardResponseDto(
                board.getId(),
                board.getBoardTitle(),
                board.getBoardContent(),
                imageUrls,
                board.getBoardCreatedAt(),
                board.getBoardUpdatedAt(),
                board.getViewCount(),
                board.getIsClean(),
                new BoardResponseDto.UserDTO(
                        board.getUser().getId(),
                        board.getUser().getNickname(),
                        board.getUser().getProfileUrl(),
                        board.getUser().getEmail()
                )
        );
    }

    private BoardDetailResponseDto convertToBoardDetailResponseDto(Board board) {
        List<String> imageUrls = new ArrayList<>();
        if (board.getImages() != null) {
            imageUrls = board.getImages().stream()
                    .map(BoardImg::getImgUrl)
                    .collect(Collectors.toList());
        }

        return new BoardDetailResponseDto(
                board.getId(),
                board.getBoardTitle(),
                board.getBoardContent(),
                imageUrls,
                board.getBoardCreatedAt(),
                board.getBoardUpdatedAt(),
                board.getViewCount(),
                board.getIsClean(),
                new BoardDetailResponseDto.UserDTO(
                        board.getUser().getId(),
                        board.getUser().getNickname(),
                        board.getUser().getProfileUrl(),
                        board.getUser().getEmail()
                ),
                board.getReplies().stream()
                        .map(reply -> new BoardDetailResponseDto.ReplyDTO(
                                reply.getId(),
                                reply.getReplyContent(),
                                reply.getReplyCreatedAt(),
                                new BoardDetailResponseDto.UserDTO(
                                        reply.getUser().getId(),
                                        reply.getUser().getNickname(),
                                        reply.getUser().getProfileUrl(),
                                        reply.getUser().getEmail()
                                ),
                                reply.getImage() != null ? reply.getImage().getImgUrl() : null
                        ))
                        .collect(Collectors.toList())
        );
    }
}