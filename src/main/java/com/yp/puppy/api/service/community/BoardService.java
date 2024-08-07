package com.yp.puppy.api.service.community;

import com.yp.puppy.api.dto.BoardResponseDto;
import com.yp.puppy.api.dto.request.community.BoardSaveDto;
import com.yp.puppy.api.dto.response.community.BoardDetailResponseDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.community.BoardRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 전체 조회 서비스
    public List<BoardResponseDto> getBoards(String sort) {
        List<Board> boards = boardRepository.findBoards(sort);
        return boards.stream()
                .map(this::convertToBoardResponseDto)
                .collect(Collectors.toList());
    }

    // 게시글 등록
    @Transactional
    public Board saveBoard(BoardSaveDto dto) {
        try {
            User user = userRepository.findById(dto.getUser().getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            Board board = Board.builder()
                    .boardTitle(dto.getBoardTitle())
                    .boardContent(dto.getBoardContent())
                    .image(dto.getImage())
                    .user(user)
                    .boardCreatedAt(LocalDateTime.now())
                    .build();

            return boardRepository.save(board);
        } catch (Exception e) {
            log.error("Error saving board: ", e);
            throw new RuntimeException("Failed to save board", e);
        }
    }

    // ID로 게시글 조회
    public BoardResponseDto getBoardById(long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));

        return convertToBoardResponseDto(board);
    }

    // 게시글 상세 조회
    public BoardDetailResponseDto getBoardDetailById(long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));

        return convertToBoardDetailResponseDto(board);
    }

    // findBoard 메서드 추가
    public Board findBoard(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));
    }

    // 게시글 DTO 변환 유틸리티 메서드
    private BoardResponseDto convertToBoardResponseDto(Board board) {
        return new BoardResponseDto(
                board.getId(),
                board.getBoardTitle(),
                board.getBoardContent(),
                board.getImage(),
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

    // 게시글 상세 DTO 변환 유틸리티 메서드
    private BoardDetailResponseDto convertToBoardDetailResponseDto(Board board) {
        return new BoardDetailResponseDto(
                board.getId(),
                board.getBoardTitle(),
                board.getBoardContent(),
                board.getImage(),
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
                                reply.getImageUrl()
                        ))
                        .collect(Collectors.toList())
        );
    }
}