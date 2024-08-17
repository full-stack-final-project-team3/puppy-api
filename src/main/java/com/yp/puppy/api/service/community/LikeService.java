package com.yp.puppy.api.service.community;

import com.yp.puppy.api.dto.response.community.BoardDetailLikeStatusDto;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.BoardReply;
import com.yp.puppy.api.entity.community.BoardSubReply;
import com.yp.puppy.api.entity.community.Like;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.community.BoardReplyRepository;
import com.yp.puppy.api.repository.community.BoardRepository;
import com.yp.puppy.api.repository.community.BoardSubReplyRepository;
import com.yp.puppy.api.repository.community.LikeRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final BoardReplyRepository replyRepository;
    private final BoardSubReplyRepository subReplyRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleLike(String type, Long id, String userId) {
        log.info("Toggling like for type: {}, id: {}, userId: {}", type, id, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Like like = likeRepository.findByTypeAndTypeIdAndUser(type, id, user);
        if (like != null) {
            likeRepository.delete(like);
            log.info("Like removed for type: {}, id: {}, userId: {}", type, id, userId);
            return false;
        } else {
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setType(type);  // type 설정

            // typeId 설정 추가
            newLike.setTypeId(id);  // id를 typeId로 설정

            switch (type) {
                case "board":
                    Board board = boardRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Board not found"));
                    newLike.setBoard(board);
                    break;
                case "reply":
                    BoardReply reply = replyRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Reply not found"));
                    newLike.setBoardReply(reply);
                    break;
                case "subReply":
                    BoardSubReply subReply = subReplyRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("SubReply not found"));
                    newLike.setBoardSubReply(subReply);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid like type");
            }
            likeRepository.save(newLike);
            log.info("Like added for type: {}, id: {}, userId: {}", type, id, userId);
            return true;
        }
    }


    @Transactional(readOnly = true)
    public Map<Long, Boolean> getLikeStatusForMultiple(String type, List<Long> ids, String userId) {
        log.info("Checking like status for multiple {}s, userId: {}", type, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Long> likedIds = likeRepository.findLikedTypeIdsByTypeAndUser(type, user);

        Map<Long, Boolean> result = new HashMap<>();
        for (Long id : ids) {
            result.put(id, likedIds.contains(id));
        }

        log.info("Like status for multiple {}s, userId: {}: {}", type, userId, result);
        return result;
    }

    @Transactional(readOnly = true)
    public boolean isLiked(String type, Long id, String userId) {
        log.info("🐶 좋아요 상태 확인 중 - 타입: {}, ID: {}, 사용자 ID: {}", type, id, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("🐶 User not found"));
        boolean liked = likeRepository.existsByTypeAndTypeIdAndUser(type, id, user);
        log.info("🐶 좋아요 상태 결과: {}", liked);
        return liked;
    }

    @Transactional(readOnly = true)
    public BoardDetailLikeStatusDto getBoardDetailLikeStatus(Long boardId, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean boardLiked = likeRepository.existsByTypeAndTypeIdAndUser("board", boardId, user);

        List<BoardReply> replies = replyRepository.findByBoardId(boardId);
        List<Long> replyIds = replies.stream().map(BoardReply::getId).collect(Collectors.toList());
        Map<Long, Boolean> replyLikes = getLikeStatusForMultiple("reply", replyIds, userId);

        List<Long> subReplyIds = replies.stream()
                .flatMap(reply -> reply.getSubReplies().stream())
                .map(BoardSubReply::getId)
                .collect(Collectors.toList());
        Map<Long, Boolean> subReplyLikes = getLikeStatusForMultiple("subReply", subReplyIds, userId);

        long boardLikeCount = getLikeCount("board", boardId);
        Map<Long, Long> replyLikeCounts = getLikeCountsForMultiple("reply", replyIds);
        Map<Long, Long> subReplyLikeCounts = getLikeCountsForMultiple("subReply", subReplyIds);

        BoardDetailLikeStatusDto dto = new BoardDetailLikeStatusDto();
        dto.setBoardLiked(boardLiked);
        dto.setBoardLikeCount(boardLikeCount);
        dto.setReplyLikes(replyLikes);
        dto.setReplyLikeCounts(replyLikeCounts);
        dto.setSubReplyLikes(subReplyLikes);
        dto.setSubReplyLikeCounts(subReplyLikeCounts);

        return dto;
    }

    public long getLikeCount(String type, Long id) {
        return likeRepository.countByTypeAndTypeId(type, id);
    }

    public Map<Long, Long> getLikeCountsForMultiple(String type, List<Long> ids) {
        List<Object[]> results = likeRepository.countLikesByTypeAndTypeIds(type, ids);
        Map<Long, Long> likeCounts = new HashMap<>();
        for (Object[] result : results) {
            Long typeId = (Long) result[0];
            Long count = (Long) result[1];
            likeCounts.put(typeId, count);
        }
        return likeCounts;
    }
}