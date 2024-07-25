package com.yp.puppy.api.repository.shop;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.user.Dog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yp.puppy.api.entity.shop.QTreats.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TreatsRepositoryCustomImpl implements TreatsRepositoryCustom{

    private final JPAQueryFactory factory;

    @Override
    public Page<Treats> findTreats(Dog userDogInfo, Pageable pageable, String sort) {

        List<Dog.Allergy> dogInfoAllergies = userDogInfo != null ? userDogInfo.getAllergies() : null;

        // 페이징을 통한 조회
        List<Treats> treatsLists = factory
                .selectFrom(treats)
                .where(dogInfoAllergies != null
                        ? treats.allergies.any().notIn((CollectionExpression<?, ? extends Treats.Allergic>) dogInfoAllergies)
                        : null)
                .orderBy(specifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 데이터 수 조회
        long count = factory
                .select(treats.count())
                .from(treats)
                .where(dogInfoAllergies != null
                        ? treats.allergies.any().notIn((CollectionExpression<?, ? extends Treats.Allergic>) dogInfoAllergies)
                        : null)
                .fetchOne();

        return new PageImpl<>(treatsLists, pageable, count);
    }

    // 정렬 조건을 처리하는 메서드
    private OrderSpecifier<?> specifier(String sort) {
        switch (sort) {
            case "title":
                return treats.treatsTitle.asc();
            case "type":
                return treats.treatsType.asc();
            default:
                return treats.treatsType.asc(); // 기본 정렬 옵션으로 변경
        }
    }
}
