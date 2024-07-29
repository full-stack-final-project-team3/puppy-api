package com.yp.puppy.api.dto.request.hotel;

import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.hotel.Review;
import com.yp.puppy.api.entity.user.User;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSaveDto {

    private String reviewContent;
    private int rate;
    private String userId;
    private String hotelId;

    public Review toEntity() {
        return Review.builder()
                .reviewContent(this.reviewContent)
                .rate(this.rate)
                .build();
    }
}
