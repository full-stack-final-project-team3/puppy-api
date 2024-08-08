package com.yp.puppy.api.controller.community;

import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.dto.BoardResponseDto;
import com.yp.puppy.api.dto.request.community.BoardSaveDto;
import com.yp.puppy.api.dto.response.community.BoardDetailResponseDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.service.community.BoardService;
import com.yp.puppy.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;
    private final TokenProvider tokenProvider; // Add this line

    // 전체 조회 요청
    @GetMapping
    public ResponseEntity<?> getList(String sort) {
        List<BoardResponseDto> boards = boardService.getBoards(sort);
        return ResponseEntity.ok().body(boards);
    }

    // 등록 요청 (파일 업로드 포함)
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> register(
            @RequestPart("dto") BoardSaveDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        log.info("🌟dto:{}", dto);
        // 파일 처리 로직 추가 (예: 파일 저장, DTO에 파일 정보 추가 등)
        if (file != null && !file.isEmpty()) {
            // 파일 저장 로직
            log.info("파일 이름: {}", file.getOriginalFilename());
        }
        // 게시글 저장 로직
        Board board = boardService.saveBoard(dto);
        return ResponseEntity.ok().body(board);
    }

    // 특정 ID의 게시글 조회 요청
    @GetMapping("/{id}")
    public ResponseEntity<?> getBoard(@PathVariable long id) {
        try {
            BoardDetailResponseDto boardDetail = boardService.getBoardDetailById(id);
            return ResponseEntity.ok().body(boardDetail);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/boardList/{userId}")
    public ResponseEntity<?> getBoardByUserId(@PathVariable String userId) {
        List<Board> foundList = userService.getMyBoardList(userId);
        if (foundList.isEmpty()) {
            return ResponseEntity.badRequest().body("작성하신 글이 없습니다.");
        }
        return ResponseEntity.ok().body(foundList);
    }

    // 삭제 요청
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            log.info("Received delete request for board id: {}", id);
            String userId = tokenProvider.validateAndGetTokenInfo(token.replace("Bearer ", "")).getUserId();
            log.info("Authenticated user id: {}", userId);

            boardService.deleteBoard(id, userId);
            log.info("Board deleted successfully");
            return ResponseEntity.ok().body(Map.of("message", "게시글이 성공적으로 삭제되었습니다."));
        } catch (EntityNotFoundException e) {
            log.error("Board not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "게시글을 찾을 수 없습니다."));
        } catch (IllegalStateException e) {
            log.error("Permission denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting board", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "게시글 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
