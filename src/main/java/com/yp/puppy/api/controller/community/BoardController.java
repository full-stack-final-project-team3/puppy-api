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
    private final TokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<?> getList(
            @RequestParam(required = false, defaultValue = "boardCreatedAt") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        List<BoardResponseDto> boards = boardService.getBoardsWithLikeCounts(sort, page, limit);
        return ResponseEntity.ok().body(boards);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> register(
            @RequestPart("dto") BoardSaveDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        log.info("🌟 dto:{}", dto);
        BoardResponseDto savedBoard = boardService.saveBoard(dto, files);
        return ResponseEntity.ok().body(savedBoard);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBoard(@PathVariable long id, @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            BoardDetailResponseDto boardDetail;
            if (token != null && !token.isEmpty()) {
                String userId = tokenProvider.validateAndGetTokenInfo(token.replace("Bearer ", "")).getUserId();
                boardDetail = boardService.getBoardDetailWithViewCount(id, userId);
            } else {
                boardDetail = boardService.getBoardDetailWithViewCount(id, null);
            }
            return ResponseEntity.ok().body(boardDetail);
        } catch (Exception e) {
            log.error("Error fetching board details: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "게시물 정보를 가져오는 중 오류가 발생했습니다: " + e.getMessage()));
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
    //
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateBoard(
            @PathVariable Long id,
            @RequestPart("dto") BoardSaveDto dto,
            @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles,
            @RequestPart(value = "imagesToDelete", required = false) List<String> imagesToDelete,
            @RequestHeader("Authorization") String token) {
        try {
            String userId = tokenProvider.validateAndGetTokenInfo(token.replace("Bearer ", "")).getUserId();
            if (!userId.equals(dto.getUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You don't have permission to update this board"));
            }

            BoardResponseDto updatedBoard = boardService.updateBoard(id, dto, newFiles, imagesToDelete);
            return ResponseEntity.ok().body(updatedBoard);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "게시글을 찾을 수 없습니다."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "게시글 수정 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    //

    @GetMapping("/search")
    public ResponseEntity<?> searchBoards(
            @RequestParam String keyword) {  // page와 limit 매개변수를 제거합니다.
        log.info("🐶 게시글 검색 요청 - keyword: {}", keyword);
        List<BoardResponseDto> searchResults = boardService.searchBoards(keyword); // page와 limit 없이 호출
        log.info("🐶 게시글 검색 결과 - {}건", searchResults.size());
        return ResponseEntity.ok().body(searchResults);
    }
    //

    @DeleteMapping("/{id}/deleteImage")
    public ResponseEntity<?> deleteImage(@PathVariable Long id,
                                         @RequestParam String imageUrl,
                                         @RequestHeader("Authorization") String token) {
        try {
            String userId = tokenProvider.validateAndGetTokenInfo(token.replace("Bearer ", "")).getUserId();
            log.info("🐶 이미지 삭제 요청 - board id: {}, 요청한 사용자: {}", id, userId);

            // 이미지 삭제 로직 호출
            boardService.deleteImage(id, imageUrl, userId);

            return ResponseEntity.ok().body(Map.of("message", "이미지가 성공적으로 삭제되었습니다."));
        } catch (EntityNotFoundException e) {
            log.error("🐶 이미지를 찾을 수 없습니다 - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "이미지를 찾을 수 없습니다."));
        } catch (Exception e) {
            log.error("🐶 이미지 삭제 중 오류 발생 - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "이미지 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    //
}