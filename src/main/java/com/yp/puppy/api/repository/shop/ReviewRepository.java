package com.yp.puppy.api.repository.shop;

import com.yp.puppy.api.entity.shop.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByTreatsId(String treatsId);
}