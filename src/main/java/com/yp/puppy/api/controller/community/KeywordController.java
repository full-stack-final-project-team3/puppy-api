package com.yp.puppy.api.controller.community;

import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.Keyword;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.community.BoardRepository;
import com.yp.puppy.api.repository.community.KeywordRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import com.yp.puppy.api.service.community.KeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/board/keywords")
@RequiredArgsConstructor
@Slf4j
public class KeywordController {



    private final KeywordService keywordService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final BoardRepository boardRepository;

    @PostMapping
    public ResponseEntity<?> registerKeyword(@RequestBody KeywordRequest request) {
        try {
            Keyword savedKeyword = keywordService.registerKeyword(request.getName());
            return ResponseEntity.ok().body(savedKeyword);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("키워드 등록 중 오류가 발생했습니다.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Keyword>> getAllKeywords() {
        List<Keyword> keywords = keywordService.getAllKeywords();
        return ResponseEntity.ok(keywords);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteKeyword(@PathVariable Long id) {
        try {
            Keyword keyword = keywordRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Keyword not found"));

            // 관련 게시글의 키워드 참조 제거
            List<Board> relatedBoards = boardRepository.findAllByKeywordId(id);
            for (Board board : relatedBoards) {
                board.setKeyword(null);
                boardRepository.save(board);
            }

            // 키워드 삭제
            keywordRepository.delete(keyword);

            return ResponseEntity.ok().body("Keyword deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting keyword: " + e.getMessage());
        }
    }
} //end controller

class KeywordRequest {
    private String name;

    // getter and setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}