package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.OrderDto;
import com.yp.puppy.api.dto.response.shop.OrderResponse;
import com.yp.puppy.api.entity.shop.Order;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.service.shop.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop/orders")
@CrossOrigin(origins = "http://localhost:8888")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto) {
        try {
            Order order = orderService.createOrder(orderDto);
            Bundle bundle = order.getCart().getBundles().get(0); // 첫 번째 번들 가져오기
            return new ResponseEntity<>(new OrderResponse(order, bundle), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Order creation failed", e);
            return new ResponseEntity<>("Order creation failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    private static class OrderResponse {
//        private Order order;
//        private Bundle bundle;
//
//        public OrderResponse(Order order, Bundle bundle) {
//            this.order = order;
//            this.bundle = bundle;
//        }
//
//        // Getters and setters
//        public Order getOrder() {
//            return order;
//        }
//
//        public void setOrder(Order order) {
//            this.order = order;
//        }
//
//        public Bundle getBundle() {
//            return bundle;
//        }
//
//        public void setBundle(Bundle bundle) {
//            this.bundle = bundle;
//        }
//    }
}
