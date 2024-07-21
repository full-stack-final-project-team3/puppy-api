package com.yp.puppy.api.repository.hotel;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yp.puppy.api.entity.hotel.Room;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yp.puppy.api.entity.hotel.QRoom.room;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoomRepositoryCustomImpl implements RoomRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public Page<Room> findRooms(Pageable pageable, String sort) {
        // 페이징을 통한 조회
        List<Room> roomList = factory
                .selectFrom(room)
                .orderBy(specifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 데이터 수 조회
        long count = factory
                .select(room.count())
                .from(room)
                .fetchOne();

        return new PageImpl<>(roomList, pageable, count);
    }

    // 정렬 조건을 처리하는 메서드
    private OrderSpecifier<?> specifier(String sort) {
        switch (sort) {
            case "name":
                return room.name.asc();
            case "content":
                return room.content.asc();
            case "price":
                return room.price.asc();
            case "type":
                return room.type.asc();
            default:
                return room.name.asc(); // 기본 정렬 옵션으로 변경
        }
    }

}
