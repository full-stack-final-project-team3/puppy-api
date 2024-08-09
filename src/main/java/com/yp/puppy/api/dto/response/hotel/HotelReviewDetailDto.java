package com.yp.puppy.api.dto.response.hotel;

import com.yp.puppy.api.entity.hotel.Review;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelReviewDetailDto {p

    private String id;
    private String reviewContent;
    private int rate;
    private LocalDateTime reviewDate;
    private String nickName;
    private String userId;
    private String hotelName;
    private String hotelId;


    public HotelReviewDetailDto(Review review) {
        this.id = review.getId();
        this.reviewContent = review.getReviewContent();
        this.rate = review.getRate();
        this.reviewDate = review.getReviewDate();
        this.nickName = review.getUser().getNickname();
        this.userId = review.getUser().getId();
        this.hotelId = review.getHotel().getHotelId();
        this.hotelName = review.getHotel().getName();
    }
}
