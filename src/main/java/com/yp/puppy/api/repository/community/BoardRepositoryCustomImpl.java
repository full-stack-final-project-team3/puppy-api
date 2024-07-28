package com.yp.puppy.api.repository.community;


import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yp.puppy.api.entity.community.Board;
import com.yp.puppy.api.entity.community.QBoard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

//구현 파트!   = 커스텀을 구현
@Repository
@RequiredArgsConstructor
@Slf4j
public class BoardRepositoryCustomImpl implements  BoardRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<Board> findBoards(String sort) {  //전체 조회
        return factory
                .selectFrom(QBoard.board)
                .orderBy(specifier(sort))
                .fetch();
    }

    //정렬 조건 처리 메서드
    private OrderSpecifier<?> specifier(String sort){
        switch (sort){
            case "date":
                return QBoard.board.boardCreatedAt.desc();
            case "title":
                return QBoard.board.boardTitle.asc();
            default:
                return null;

        }
    }

}
