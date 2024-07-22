package com.yp.puppy.api.service.hotel;

import com.yp.puppy.api.dto.request.hotel.ReservationSaveDto;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.hotel.Reservation;
import com.yp.puppy.api.entity.hotel.Room;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.hotel.HotelRepository;
import com.yp.puppy.api.repository.hotel.ReservationRepository;
import com.yp.puppy.api.repository.hotel.RoomRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    // 예약 생성 중간처리
    public Reservation createReservation(ReservationSaveDto dto) {
        Room foundRoom = roomRepository.findById(dto.getRoomId()).orElseThrow();
        Hotel foundHotel = hotelRepository.findById(dto.getHotelId()).orElseThrow();
        User foundUser = userRepository.findById(dto.getUserId()).orElseThrow();

        Reservation reservation = Reservation.builder()
                .reservationAt(dto.getReservationAt())
                .reservationEndAt(dto.getReservationEndAt())
                .price(dto.getPrice())
                .cancelled(dto.getCancelled())
                .room(foundRoom)
                .hotel(foundHotel)
                .user(foundUser)
                .build();

        return reservationRepository.save(reservation);

    }


    // 예약 조회 중간처리


    // 예약 취소 중간처리

}
