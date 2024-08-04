package com.yp.puppy.api.repository.shop;

import com.yp.puppy.api.entity.shop.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, String> {
}