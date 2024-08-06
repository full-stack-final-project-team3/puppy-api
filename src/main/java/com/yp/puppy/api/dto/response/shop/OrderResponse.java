package com.yp.puppy.api.dto.response.shop;

import com.yp.puppy.api.entity.shop.Order;
import com.yp.puppy.api.entity.shop.Bundle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Order order;
    private Bundle bundle;
}
