package com.yp.puppy.api.repository.shop;

import com.yp.puppy.api.entity.shop.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
    // 추가적인 쿼리 메서드를 정의할 수 있습니다.
}
