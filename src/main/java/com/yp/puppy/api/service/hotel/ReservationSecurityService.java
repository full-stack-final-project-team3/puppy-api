package com.yp.puppy.api.service.hotel;

import com.yp.puppy.api.entity.hotel.Reservation;
import com.yp.puppy.api.repository.hotel.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationSecurityService {
    private final ReservationRepository reservationRepository;

    public boolean isOwner(Authentication authentication, String reservationId) {
        String userId = authentication.getName();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
        return reservation.getUser().getId().equals(userId);
    }
}
