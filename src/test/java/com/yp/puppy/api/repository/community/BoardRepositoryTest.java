package com.yp.puppy.api.repository.community;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yp.puppy.api.entity.community.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.yp.puppy.api.entity.community.QBoard.board;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@Transactional
class BoardRepositoryTest {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    JPAQueryFactory factory;

    // Create, Read, Update, Delete

    @Test
    @DisplayName("저장 테스트(Ok)")
    void WriteBoardTest() {
        //given
        Board board1 = Board
                .builder()
                .boardContent("내용1")
                .boardCreatedAt(LocalDateTime.now())
                .boardTitle("제목1")
                .boardUpdatedAt(LocalDateTime.now())
                .isClean(1)
                .viewCount(0)
                .build();

        //when
        boardRepository.save(board1);
        System.out.println("😀board1 = " + board1);
        Optional<Board> byId = boardRepository.findById(board1.getId());
        System.out.println("🤣byId = " + byId);


    }

    @Test
    @DisplayName("조회 테스트 - 모든 게시글")
    void findAllBoardsTest() {
        List<Board> all = boardRepository.findAll();
        
        all.forEach(board-> System.out.println("😃board = " + board));
        //when
//        List<Board> boards =
//                factory.select(board)
//                                .from(board)
//                                        .fetch();
//        boards.forEach(board -> System.out.println("😀board = " + board));
    }

    @Test
    @DisplayName("수정  테스트 진행")
    void boardUpdateTest() {

        Board board1 = Board
                .builder()
                .boardContent("내용1")
                .boardCreatedAt(LocalDateTime.now())
                .boardTitle("제목1")
                .boardUpdatedAt(LocalDateTime.now())
                .isClean(1)
                .viewCount(0)
                .build();

        //when
        boardRepository.save(board1);
        System.out.println("👽board1 = " + board1);

        Board board2 = boardRepository.findById(board1.getId()).orElse(null);
        board2.setBoardContent("수정 테스트₩1");
        board2.setBoardTitle("수정된제목1");
        
       Board board3 = boardRepository.save(board2);
        System.out.println("👽board3 = " + board3);
//        String id = board3.getId();
//        System.out.println("삭제 진행");
//        boardRepository.deleteById(id);
//
//        Board board4 = boardRepository.findById(id).orElse(null);
//        System.out.println("👽삭제 후 조회 = " + board4);
//        assertNull(board4);
        //then
    }

    @Test
    @DisplayName("삭제 테스트")
    void removeTest() {
        //given
        Board board1 = Board
                .builder()
                .boardContent("내용12")
                .boardCreatedAt(LocalDateTime.now())
                .boardTitle("제목12")
                .boardUpdatedAt(LocalDateTime.now())
                .isClean(1)
                .viewCount(0)
                .build();

        //when
        boardRepository.save(board1);

        Board board3 = boardRepository.save(board1);
        System.out.println("👽board3 = " + board3);
        String id = board3.getId();
        System.out.println("삭제 진행");
        boardRepository.deleteById(id);

        Board board4 = boardRepository.findById(id).orElse(null);
        System.out.println("👽삭제 후 조회 = " + board4);
        assertNull(board4);

        //when

        //then
    }
}