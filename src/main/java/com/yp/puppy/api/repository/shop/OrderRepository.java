package com.yp.puppy.api.repository.shop;

import com.yp.puppy.api.entity.shop.Order;
import com.yp.puppy.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUser(User user);
    // 아이디로 주문 내역 조회
    List<Order> findByUserIdOrderByOrderDateTimeDesc(String userId);

    // 일 / 주 / 월 지출 포인트 조회
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.orderDateTime BETWEEN :start AND :end")
    Long sumTotalPriceByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
