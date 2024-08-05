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
                                   Dog.DogAgeType dogAgeType,
                                   Pageable pageable,
                                   String sort // 타입 매개변수
    ) {
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

        // 타입 조건 추가
        Treats.TreatsType type = getTreatsType(sort); // sort를 통해 타입 가져오기
        if (type != null) {
            builder.and(treats.treatsType.eq(type)); // 지정된 타입으로 필터링
        }

        // 연령층 조건 추가
        if (dogAgeType != null) {
            switch (dogAgeType) {
                case BABY:
                    builder.and(treats.treatsAgeType.in(Treats.TreatsAgeType.BABY, Treats.TreatsAgeType.ALL));
                    break;
                case OLD:
                    builder.and(treats.treatsAgeType.in(Treats.TreatsAgeType.OLD, Treats.TreatsAgeType.ALL));
                    break;
                case MIDDLE:
                    builder.and(treats.treatsAgeType.ne(Treats.TreatsAgeType.BABY)); // BABY 제외
                    builder.and(treats.treatsAgeType.ne(Treats.TreatsAgeType.OLD)); // OLD 제외
                    break;
                default:
                    break;
            }
        }

        // 페이징을 통한 조회
        List<Treats> treatsLists = factory
                .selectFrom(treats)
                .where(builder) // 빌더를 사용하여 조건 추가
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

    // 간식 타입을 반환하는 메서드
    private Treats.TreatsType getTreatsType(String type) {
        switch (type.toLowerCase()) { // 대소문자 구분 없이 처리
            case "dry":
                return Treats.TreatsType.DRY;
            case "wet":
                return Treats.TreatsType.WET;
            case "gum":
                return Treats.TreatsType.GUM;
            case "kibble":
                return Treats.TreatsType.KIBBLE;
            case "supps":
                return Treats.TreatsType.SUPPS;
            default:
                return Treats.TreatsType.DRY; // 유효하지 않은 타입
        }
    }

}