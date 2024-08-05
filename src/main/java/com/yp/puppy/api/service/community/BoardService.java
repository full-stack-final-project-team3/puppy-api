package com.yp.puppy.api.service.community;

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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {
private final BoardRepository boardRepository;
private final UserRepository userRepository;
    //    전체 조회 서비스
    public List<Board> getBoards(String sort){
        return boardRepository.findBoards(sort);
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
