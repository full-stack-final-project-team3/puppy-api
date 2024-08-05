package com.yp.puppy.api.service.community;

import com.yp.puppy.api.dto.BoardResponseDto;
import com.yp.puppy.api.dto.request.community.BoardSaveDto;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {
private final BoardRepository boardRepository;
private final UserRepository userRepository;
    //    전체 조회 서비스

    public List<BoardResponseDto> getBoards(String sort) {
        List<Board> boards = boardRepository.findBoards(sort);
        return boards.stream()
                .map(board -> new BoardResponseDto(
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
                ))
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
    public Board getBoardById(long id) {
        Optional<Board> boardOptional = boardRepository.findById(id);
        if (boardOptional.isPresent()) {
            return boardOptional.get();
        } else {
            throw new RuntimeException("Board not found with id: " + id);
        }
    }

}
