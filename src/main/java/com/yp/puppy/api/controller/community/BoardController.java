package com.yp.puppy.api.controller.community;

import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.dto.request.community.BoardSaveDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.service.community.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // 등록 요청 (파일 업로드 포함)
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> register(
            @RequestPart("dto") BoardSaveDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
       log.info("🌟dto:{}",dto);
        // 파일 처리 로직 추가 (예: 파일 저장, DTO에 파일 정보 추가 등)
        if (file != null && !file.isEmpty()) {
            // 파일 저장 로직
            log.info("파일 이름: {}", file.getOriginalFilename());
        }
        // 게시글 저장 로직
         Board board= boardService.saveBoard(dto);

        return ResponseEntity.ok().body(board);
    }

    // 특정 ID의 게시글 조회 요청
    @GetMapping("/{id}")
    public ResponseEntity<?> getBoard(@PathVariable long id) {
        try {
            Board board = boardService.getBoardById(id);
            return ResponseEntity.ok().body(board);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
