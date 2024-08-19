package com.yp.puppy.api.repository.community;
import org.springframework.data.domain.Pageable;
import com.yp.puppy.api.entity.community.Board;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//커스텀 레포지토리 상속 시킴!
public interface BoardRepository extends JpaRepository<Board,Long>,BoardRepositoryCustom {
   //검색 쿼리 추가
    @Query("SELECT b FROM Board b WHERE upper(b.boardTitle) LIKE upper(concat('%', ?1, '%'))")
    List<Board> searchByTitleContaining(String keyword);

}
