package com.yp.puppy.api.controller.community;

import com.yp.puppy.api.dto.request.community.BoardSaveDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.service.community.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //무조건 ㅇㅣ걸로
@RequestMapping("/boards")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin  //
public class BoardController {

    private final BoardService boardService;

    //    전체 조회 요청
    @GetMapping
    public ResponseEntity<?> getList(String sort) {
        List<Board> boards = boardService.getBoards(sort);
        return ResponseEntity.ok().body(boards);
    }
    //등록 요청
    @PostMapping
    public ResponseEntity<?> register(@RequestBody BoardSaveDto dto){
     List<Board> boards = boardService.saveBoard(dto);
     return ResponseEntity.ok().body(boards);
    }
}
