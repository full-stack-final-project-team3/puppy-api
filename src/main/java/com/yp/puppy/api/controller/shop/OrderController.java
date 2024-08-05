package com.yp.puppy.api.controller.shop;

import com.yp.puppy.api.dto.request.shop.OrderDto;
import com.yp.puppy.api.entity.shop.Order;
import com.yp.puppy.api.repository.shop.OrderRepository;
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
    public ResponseEntity<Order> createOrder(@RequestBody OrderDto orderDto) {
        Order order = orderService.createOrder(orderDto);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

}
