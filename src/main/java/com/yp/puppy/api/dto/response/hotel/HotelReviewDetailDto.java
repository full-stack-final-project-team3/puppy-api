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
public class HotelReviewDetailDto {

    private String id;
    private String reviewContent;
    private int rate;
    private LocalDateTime reviewDate;
    private String nickName;


    public HotelReviewDetailDto(Review review) {
        this.id = review.getId();
        this.reviewContent = review.getReviewContent();
        this.rate = review.getRate();
        this.reviewDate = review.getReviewDate();
        this.nickName = review.getUser().getNickname();
    }
}
