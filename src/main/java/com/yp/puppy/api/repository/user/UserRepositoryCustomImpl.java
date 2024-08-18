package com.yp.puppy.api.repository.user;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yp.puppy.api.entity.hotel.QReservation;
import com.yp.puppy.api.entity.shop.QOrder;
import com.yp.puppy.api.entity.user.QUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory factory;


    @Override
    public long countUsersByPeriod(LocalDateTime start, LocalDateTime end) {
        QUser user = QUser.user;
        return factory
                .selectFrom(user)
                .where(user.createdAt.between(start, end))
                .fetchCount();
    }


    @Override
    public Long sumTotalPriceShopByDateRange(LocalDateTime dayStart, LocalDateTime dayEnd) {
        QOrder order = QOrder.order;

        return factory.select(order.totalPrice.sum())
                .from(order)
                .where(order.orderDateTime.between(dayStart, dayEnd))
                .fetchOne();
    }

    @Override
    public Long sumTotalPriceHotelByDateRange(LocalDateTime dayStart, LocalDateTime dayEnd) {
        QReservation reservation = QReservation.reservation;

        return factory.select(reservation.price.sum())
                .from(reservation)
                .where(reservation.reservationCreateAt.between(dayStart, dayEnd))
                .fetchOne();
    }

    @Override
    public List<Long> getPointTotalDay(LocalDateTime start, LocalDateTime end) {
        List<Long> dailyTotals = new ArrayList<>();

        for (LocalDateTime date = start; date.isBefore(end.plusDays(1)); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = date.toLocalDate().atTime(23, 59, 59);

            Long hotelExpense = sumTotalPriceHotelByDateRange(dayStart, dayEnd);
            Long shopExpense = sumTotalPriceShopByDateRange(dayStart, dayEnd);

            dailyTotals.add(hotelExpense + shopExpense);
        }

        return dailyTotals;
    }
}
