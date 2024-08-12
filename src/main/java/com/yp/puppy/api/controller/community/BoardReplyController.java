package com.yp.puppy.api.controller.community;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yp.puppy.api.dto.response.community.BoardDetailResponseDto;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.service.community.BoardReplyService;
import com.yp.puppy.api.service.community.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/board")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
public class BoardReplyController {

    private final BoardReplyService boardReplyService;
    private final BoardService boardService;
    private final ObjectMapper objectMapper;

    @PostMapping("/{boardId}/comments")
    public ResponseEntity<?> createReply(
            @PathVariable Long boardId,
            @RequestParam("content") String content,
            @RequestParam("user") String userJson,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            log.info("Creating reply for board ID: {}", boardId);
            log.debug("Reply content: {}", content);
            log.debug("User JSON: {}", userJson);

            User user = objectMapper.readValue(userJson, User.class);
            BoardDetailResponseDto.ReplyDTO newReply = boardReplyService.saveReply(boardId, content, user, image);
            log.info("New reply: {}", newReply);

            BoardDetailResponseDto updatedBoard = boardService.getBoardDetailById(boardId);
            log.info("Reply created successfully for board ID: {}", boardId);

            return ResponseEntity.ok(updatedBoard);
        } catch (Exception e) {
            log.error("Error creating reply: ", e);
            return ResponseEntity.status(500).body("Failed to create reply");
        }
    }

    @PutMapping("/{boardId}/comments/{replyId}")
    public ResponseEntity<?> updateReply(
            @PathVariable Long boardId,
            @PathVariable Long replyId,
            @RequestBody Map<String, Object> payload) {

        try {
            log.info("Updating reply. Board ID: {}, Reply ID: {}", boardId, replyId);
            String content = (String) payload.get("content");
            String userId = (String) payload.get("userId");

            log.debug("Update payload: Content: {}, UserId: {}", content, userId);

            BoardDetailResponseDto.ReplyDTO updatedReply = boardReplyService.updateReply(replyId, content, userId);
            BoardDetailResponseDto updatedBoard = boardService.getBoardDetailById(boardId);

            log.info("Reply updated successfully. Reply ID: {}", replyId);

            return ResponseEntity.ok(updatedBoard);
        } catch (Exception e) {
            log.error("Error updating reply: ", e);
            return ResponseEntity.status(500).body("Failed to update reply");
        }
    }

    @DeleteMapping("/{boardId}/comments/{replyId}")
    public ResponseEntity<?> deleteReply(
            @PathVariable Long boardId,
            @PathVariable Long replyId,
            @RequestParam("userId") String userId) {

        try {
            log.info("Deleting reply. Board ID: {}, Reply ID: {}, User ID: {}", boardId, replyId, userId);

            boardReplyService.deleteReply(replyId, userId);
            BoardDetailResponseDto updatedBoard = boardService.getBoardDetailById(boardId);

            log.info("Reply deleted successfully. Reply ID: {}", replyId);

            return ResponseEntity.ok(updatedBoard);
        } catch (EntityNotFoundException e) {
            log.error("Reply not found: ", e);
            return ResponseEntity.status(404).body("Reply not found");
        } catch (IllegalArgumentException e) {
            log.error("Unauthorized delete attempt: ", e);
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting reply: ", e);
            return ResponseEntity.status(500).body("Failed to delete reply");
        }
    }
}
