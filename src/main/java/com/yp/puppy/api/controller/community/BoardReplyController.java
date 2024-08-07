package com.yp.puppy.api.controller.community;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.BoardReply;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.service.community.BoardReplyService;
import com.yp.puppy.api.service.community.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            log.info("🧤Creating reply for board ID: {}", boardId);
            log.debug("🧤Reply content: {}", content);
            log.debug("🧤User JSON: {}", userJson);

            User user = objectMapper.readValue(userJson, User.class);
            BoardReply newReply = boardReplyService.saveReply(boardId, content, user, image);
            log.info("🌈newReply: {}", newReply);

            // 댓글이 성공적으로 저장된 후, 전체 게시글 정보를 다시 조회합니다.
            Board updatedBoard = boardService.findBoard(boardId);
            log.info("🧤Reply created successfully for board ID: {}", boardId);

            // 업데이트된 전체 게시글 정보를 반환합니다.
            return ResponseEntity.ok(updatedBoard);
        } catch (Exception e) {
            log.error("Error creating reply for board ID: " + boardId, e);
            return ResponseEntity.badRequest().body("Error creating reply: " + e.getMessage());
        }
    }
}