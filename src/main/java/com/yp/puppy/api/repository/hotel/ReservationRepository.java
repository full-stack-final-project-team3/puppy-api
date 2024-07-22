package com.yp.puppy.api.repository.hotel;

import com.yp.puppy.api.entity.hotel.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
}
