package com.yp.puppy.api.repository.community;

import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.BoardView;
import com.yp.puppy.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardViewRepository extends JpaRepository<BoardView, Long> {
    Optional<BoardView> findByUserAndBoard(User user, Board board);

    // 특정 Board와 연관된 모든 BoardView 엔티티들을 삭제하는 메서드
    void deleteByBoard(Board board);
}