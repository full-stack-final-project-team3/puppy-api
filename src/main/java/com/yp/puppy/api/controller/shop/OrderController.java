package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.OrderDto;
import com.yp.puppy.api.dto.response.shop.OrderDetailResponse;
import com.yp.puppy.api.dto.response.shop.OrderResponse;
import com.yp.puppy.api.entity.shop.Order;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.service.shop.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderDto orderDto) {
        try {
            log.info("Received OrderDto: {}", orderDto); // 전달된 DTO 확인 로그
            Order order = orderService.createOrder(orderDto);
            return new OrderResponse(order);
        } catch (Exception e) {
            log.error("주문 생성 실패", e);
            throw new RuntimeException("주문 생성 실패~");
        }
    }

    @GetMapping("/{orderId}")
    public OrderDetailResponse orderDetail(@PathVariable(value = "orderId") String orderId) {
        try {
            log.info("orderDetail: {}", orderId); // 전달된 DTO 확인 로그
            return orderService.getOrderDetail(orderId);
        } catch (Exception e) {
            log.error("주문 내역을 찾지 못함", e);
            throw new RuntimeException("주문 내역을 찾지 못함");
        }
    }

    @GetMapping("/user/{userId}")
    public List<OrderResponse> getOrderHistory(@PathVariable String userId) {
        try {
            return orderService.getOrderHistory(userId);
        } catch (Exception e) {
            log.error("주문 내역을 찾지 못함", e);
            throw new RuntimeException("주문 내역을 찾지 못함");
        }
    }

    //주문 취소
    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable String orderId) {
        try {
            orderService.cancelOrder(orderId);
            return "주문이 취소됨";
        } catch (Exception e) {
            log.error("주문 취소 실패함", e);
            throw new RuntimeException("주문 취소 실패함");
        }
    }
}