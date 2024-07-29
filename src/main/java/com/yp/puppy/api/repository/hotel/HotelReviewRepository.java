package com.yp.puppy.api.repository.hotel;

import com.yp.puppy.api.entity.hotel.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelReviewRepository extends JpaRepository<Review, String> {
}
