package com.yp.puppy.api.repository.user;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepositoryCustom {

    long countUsersByPeriod(LocalDateTime start, LocalDateTime end);

    Long sumTotalPriceShopByDateRange(LocalDateTime dayStart, LocalDateTime dayEnd);

    Long sumTotalPriceHotelByDateRange(LocalDateTime dayStart, LocalDateTime dayEnd);

    List<Long> getPointTotalDay(LocalDateTime start, LocalDateTime end);


}
