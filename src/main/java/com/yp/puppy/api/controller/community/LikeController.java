package com.yp.puppy.api.controller.community;

import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.dto.response.community.BoardDetailLikeStatusDto;
import com.yp.puppy.api.service.community.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class LikeController {
    private final LikeService likeService;
    private final TokenProvider tokenProvider;
    //처리
    @PostMapping("/{type}/{id}")
    public ResponseEntity<?> toggleLike(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        log.info("Toggling like for type: {}, id: {}", type, id);
        if (!Arrays.asList("board", "reply", "subReply").contains(type)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid type"));
        }
        try {
            String userId = tokenProvider.validateAndGetTokenInfo(token.replace("Bearer ", "")).getUserId();
            boolean isLiked = likeService.toggleLike(type, id, userId);
            return ResponseEntity.ok().body(Map.of("liked", isLiked));
        } catch (EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error toggling like", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "좋아요 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    //안씀
    @GetMapping("/{type}/{id}")
    public ResponseEntity<?> getLikeStatus(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        log.info("🐶 좋아요 상태 가져오는 중 - 타입: {}, ID: {}", type, id);
        if (!Arrays.asList("board", "reply", "subReply").contains(type)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid type"));
        }
        try {
            String userId = tokenProvider.validateAndGetTokenInfo(token.replace("Bearer ", "")).getUserId();
            boolean isLiked = likeService.isLiked(type, id, userId);
            log.info("🐶 좋아요 상태 응답: {}", isLiked);
            return ResponseEntity.ok().body(Map.of("liked", isLiked));
        } catch (EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting like status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "좋아요 상태 확인 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    //전체 상태 전송
    @PostMapping("/{type}/status")
    public ResponseEntity<?> getMultipleLikeStatus(
            @PathVariable String type,
            @RequestBody List<Long> ids,
            @RequestHeader("Authorization") String token) {
        log.info("Getting like status for multiple {}s: {}", type, ids);
        if (!Arrays.asList("board", "reply", "subReply").contains(type)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid type"));
        }
        try {
            String userId = tokenProvider.validateAndGetTokenInfo(token.replace("Bearer ", "")).getUserId();
            Map<Long, Boolean> likeStatus = likeService.getLikeStatusForMultiple(type, ids, userId);
            return ResponseEntity.ok().body(likeStatus);
        } catch (Exception e) {
            log.error("Error getting multiple like status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "좋아요 상태 확인 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/board/{boardId}/like-status")
    public ResponseEntity<?> getBoardDetailLikeStatus(
            @PathVariable Long boardId,
            @RequestHeader("Authorization") String token) {
        try {
            String userId = tokenProvider.validateAndGetTokenInfo(token.replace("Bearer ", "")).getUserId();
            BoardDetailLikeStatusDto likeStatus = likeService.getBoardDetailLikeStatus(boardId, userId);
            return ResponseEntity.ok().body(likeStatus);
        } catch (Exception e) {
            log.error("Error getting board detail like status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "좋아요 상태 확인 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}