package com.yp.puppy.api.repository.user;

import java.time.LocalDateTime;

public interface UserRepositoryCustom {

    long countUsersByPeriod(LocalDateTime start, LocalDateTime end);
}
