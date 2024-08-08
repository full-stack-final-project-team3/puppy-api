package com.yp.puppy.api.dto.request.hotel;

import com.yp.puppy.api.entity.hotel.CancellationStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
public class ReservationSaveDto {
    private String userId;
    private String roomId;
    private String hotelId;
    private OffsetDateTime reservationAt;
    private OffsetDateTime reservationEndAt;
    private CancellationStatus cancelled;
    private long price;
}
