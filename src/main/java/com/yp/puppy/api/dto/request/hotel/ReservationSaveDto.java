package com.yp.puppy.api.dto.request.hotel;

import com.yp.puppy.api.entity.hotel.CancellationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationSaveDto {
    private String userId;
    private String roomId;
    private String hotelId;
    private LocalDateTime reservationAt;
    private LocalDateTime reservationEndAt;
    private long price;
    private CancellationStatus cancelled;
}
