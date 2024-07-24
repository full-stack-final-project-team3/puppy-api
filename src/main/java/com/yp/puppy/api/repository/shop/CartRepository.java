package com.yp.puppy.api.repository.shop;

import com.yp.puppy.api.entity.shop.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, String> {


}
