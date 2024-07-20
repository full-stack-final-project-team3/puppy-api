package com.yp.puppy.api.repository.hotel;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yp.puppy.api.entity.hotel.Hotel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yp.puppy.api.entity.hotel.QHotel.hotel;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HotelRepositoryCustomImpl implements HotelRepositoryCustom {

    private final JPAQueryFactory factory;


    @Override
    public Page<Hotel> findHotels(Pageable pageable, String sort) {
        // 페이징을 통한 조회
        List<Hotel> hotelList = factory
                .selectFrom(hotel)
                .orderBy(specifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 데이터 수 조회
        long count = factory
                .select(hotel.count())
                .from(hotel)
                .fetchOne();

        return new PageImpl<>(hotelList, pageable, count);
    }

    // 정렬 조건을 처리하는 메서드
    private OrderSpecifier<?> specifier(String sort) {
        switch (sort) {
            case "name":
                return hotel.name.asc();
            case "description":
                return hotel.description.asc();
            case "price":
                return hotel.price.asc();
            case "location":
                return hotel.location.asc();
            default:
                return hotel.name.asc(); // 기본 정렬 옵션으로 변경
        }
    }

}
