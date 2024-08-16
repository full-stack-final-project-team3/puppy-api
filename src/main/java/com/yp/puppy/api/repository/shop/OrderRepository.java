package com.yp.puppy.api.repository.shop;

import com.yp.puppy.api.entity.shop.Order;
import com.yp.puppy.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUser(User user);
    // 아이디로 주문 내역 조회
    //List<Order> findByUserId(String userId);
}
