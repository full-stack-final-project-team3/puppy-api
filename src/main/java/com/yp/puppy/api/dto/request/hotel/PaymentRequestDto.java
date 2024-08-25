package com.yp.puppy.api.dto.request.hotel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private String userId;
    private String hotelId;
    private String roomId;
    private long totalPrice;
    private String startDate;
    private String endDate;
}
