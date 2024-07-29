package com.yp.puppy.api.service.community;

import com.yp.puppy.api.dto.request.community.BoardSaveDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.repository.community.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {
private final BoardRepository boardRepository;
    //    전체 조회 서비스
    public List<Board> getBoards(String sort){
        return boardRepository.findBoards(sort);
    }
    // 게시글 등록
    public List<Board> saveBoard(BoardSaveDto dto) {
        Board saveBoard = boardRepository.save(dto.toEntity());
        log.debug("saved board:{}",saveBoard);
        return getBoards("date");
    }
    // ID로 게시글 조회
    public Board getBoardById(String id) {
        Optional<Board> boardOptional = boardRepository.findById(id);
        if (boardOptional.isPresent()) {
            return boardOptional.get();
        } else {
            throw new RuntimeException("Board not found with id: " + id);
        }
    }

}
