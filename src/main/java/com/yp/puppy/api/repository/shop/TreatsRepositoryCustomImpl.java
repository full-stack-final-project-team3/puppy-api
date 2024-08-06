package com.yp.puppy.api.repository.shop;

import com.querydsl.core.BooleanBuilder;
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
public class TreatsRepositoryCustomImpl implements TreatsRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public Page<Treats> findTreats(List<Treats.Allergic> userDogAllergiesInfo,
                                   Dog.DogSize dogSize,
                                   Pageable pageable, String sort,
                                   Treats.TreatsType treatsType) {
        // 조건 빌더 생성
        BooleanBuilder builder = new BooleanBuilder();

        // 알레르지 조건 추가
        if (userDogAllergiesInfo != null && !userDogAllergiesInfo.isEmpty()) {
            builder.and(treats.allergieList.any().in(userDogAllergiesInfo).not()); // 유저의 알레르지 리스트와 겹치지 않는 경우
        }

        // 강아지 크기 조건 추가
        if (dogSize != null) {
            builder.and(treats.dogSize.eq(dogSize)); // 강아지 크기가 동일한 간식 필터링
        }

        // TreatsType이 null이 아닐 경우에만 조건 추가
        if (treatsType != null) {
            builder.and(treats.treatsType.eq(treatsType)); // 특정 TreatsType 필터링
        }

        // 페이징을 통한 조회
        List<Treats> treatsLists = factory
                .selectFrom(treats)
                .where(builder) // 빌더를 사용하여 조건 추가
                .orderBy(specifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 데이터 수 조회
        long count = factory
                .select(treats.count())
                .from(treats)
                .where(builder) // 빌더를 사용하여 조건 추가
                .fetchOne();

        return new PageImpl<>(treatsLists, pageable, count);
    }

    // 정렬 조건을 처리하는 메서드
    private OrderSpecifier<?> specifier(String sort) {
        switch (sort) {
            case "title":
                return treats.treatsTitle.asc();
            default:
                return treats.treatsTitle.asc(); // 기본 정렬 옵션으로 변경
        }
    }


}