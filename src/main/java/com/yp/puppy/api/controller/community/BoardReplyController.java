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
    import java.time.LocalDateTime;
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
                User user = objectMapper.readValue(userJson, User.class);
                BoardDetailResponseDto.ReplyDTO newReply = boardReplyService.saveReply(boardId, content, user, image);
                return ResponseEntity.ok(newReply);
            } catch (Exception e) {
                log.error("Error creating reply: ", e);
                return ResponseEntity.status(500).body("Failed to create reply: " + e.getMessage());
            }
        }

        @PutMapping("/{boardId}/comments/{replyId}")
        public ResponseEntity<?> updateReply(
                @PathVariable Long boardId,
                @PathVariable Long replyId,
                @RequestBody Map<String, Object> payload) {
            try {
                String content = (String) payload.get("content");
                String userId = (String) payload.get("userId");
                log.debug("Updating reply. BoardId: {}, ReplyId: {}, Content: {}, UserId: {}", boardId, replyId, content, userId);
                BoardDetailResponseDto.ReplyDTO updatedReply = boardReplyService.updateReply(replyId, content, userId);
                log.debug("Reply updated successfully: {}", updatedReply);
                return ResponseEntity.ok(updatedReply);
            } catch (EntityNotFoundException e) {
                log.error("Reply not found: ", e);
                return ResponseEntity.status(404).body("Reply not found");
            } catch (IllegalArgumentException e) {
                log.error("Unauthorized update attempt: ", e);
                return ResponseEntity.status(403).body(e.getMessage());
            } catch (Exception e) {
                log.error("Error updating reply: ", e);
                return ResponseEntity.status(500).body("Failed to update reply: " + e.getMessage());
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

        //Sub Reply 작성
        @PostMapping("/{boardId}/comments/{replyId}/subReplies")
        public ResponseEntity<?> createSubReply(
                @PathVariable Long boardId,
                @PathVariable Long replyId,
                @RequestParam("subReplyContent") String content, // 수정된 부분
                @RequestParam("user") String userJson,
                @RequestParam(value = "image", required = false) MultipartFile image) {
            try {
                User user = objectMapper.readValue(userJson, User.class);
                BoardDetailResponseDto.SubReplyDTO newSubReply = boardReplyService.saveSubReply(replyId, content, user, image);

                // 생성 시간을 ISO 8601 형식의 문자열로 변환
                newSubReply.setSubReplyCreatedAt(LocalDateTime.now());

                return ResponseEntity.ok(newSubReply);
            } catch (Exception e) {
                log.error("서브 댓글 생성 중 오류 발생: ", e);
                return ResponseEntity.status(500).body("서브 댓글 생성에 실패했습니다.");
            }
        }

        @PutMapping("/{boardId}/comments/{replyId}/subReplies/{subReplyId}")
        public ResponseEntity<?> updateSubReply(
                @PathVariable Long boardId,
                @PathVariable Long replyId,
                @PathVariable Long subReplyId,
                @RequestBody Map<String, Object> payload) {
            try {
                String content = (String) payload.get("content");
                String userId = (String) payload.get("userId");
                BoardDetailResponseDto.SubReplyDTO updatedSubReply = boardReplyService.updateSubReply(subReplyId, content, userId);
                return ResponseEntity.ok(updatedSubReply);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Failed to update sub-reply");
            }
        }

        @DeleteMapping("/{boardId}/comments/{replyId}/subReplies/{subReplyId}")
        public ResponseEntity<?> deleteSubReply(
                @PathVariable Long boardId,
                @PathVariable Long replyId,
                @PathVariable Long subReplyId,
                @RequestParam("userId") String userId) {
            try {
                boardReplyService.deleteSubReply(subReplyId, userId);
                return ResponseEntity.ok().body("Sub-reply deleted successfully");
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Failed to delete sub-reply");
            }
        }
    }
