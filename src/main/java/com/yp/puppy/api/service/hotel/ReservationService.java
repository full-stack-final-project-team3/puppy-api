package com.yp.puppy.api.service.hotel;

import com.yp.puppy.api.dto.request.hotel.ReservationSaveDto;
import com.yp.puppy.api.dto.response.hotel.ReservationOneDto;
import com.yp.puppy.api.entity.hotel.CancellationStatus;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

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

        // 날짜 중복 체크
        LocalDateTime startLocalDateTime = dto.getReservationAt().atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime().with(LocalTime.of(14, 0));
        LocalDateTime endLocalDateTime = dto.getReservationEndAt().atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime().with(LocalTime.of(11, 0));

        if (isReservationDateOverlap(dto.getRoomId(), startLocalDateTime, endLocalDateTime)) {
            throw new IllegalArgumentException("예약 날짜가 중복됩니다.");
        }

        long roomPrice = foundRoom.getPrice();

        log.info("초기 포인트: {}", foundUser.getPoint());
        foundUser.withdrawalPoints((int) roomPrice);
        log.info("포인트 차감 후: {}", foundUser.getPoint());

        userRepository.save(foundUser);
        log.info("포인트 저장 후: {}", userRepository.findById(foundUser.getId()).orElseThrow().getPoint());

        Reservation reservation = Reservation.builder()
                .reservationAt(startLocalDateTime)
                .reservationEndAt(endLocalDateTime)
                .cancelled(CancellationStatus.SUCCESS)
                .price(dto.getPrice())
                .room(foundRoom)
                .hotel(foundHotel)
                .user(foundUser)
                .build();

        return reservationRepository.save(reservation);
    }

    // 중복날자 체크 메서드
    private boolean isReservationDateOverlap(String roomId, LocalDateTime reservationAt, LocalDateTime reservationEndAt) {
        List<Reservation> existingReservations = reservationRepository.findByRoom_RoomIdAndReservationEndAtAfterAndReservationAtBefore(
                roomId, reservationAt, reservationEndAt);

        return !existingReservations.isEmpty();
    }

    // 예약 조회 중간처리
    public ReservationOneDto getDetailReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        return new ReservationOneDto(reservation);
    }

    // 내가 예약한 객실 전체조회 - 취소되지 않은 예약만 조회
    public List<ReservationOneDto> getReservationsByUserId(String userId) {
        log.info("Fetching reservations for userId: {}", userId);
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        log.info("Fetched reservations: {}", reservations);

        return reservations.stream()
                .filter(reservation -> reservation.getCancelled() != CancellationStatus.CANCELLED)
                .map(ReservationOneDto::new)
                .collect(Collectors.toList());
    }

    // 예약 취소 중간처리
    public void deleteReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        User user = reservation.getUser();

        user.addPoints((int) reservation.getPrice());
        userRepository.save(user);

        log.info("예약 취소 후 포인트 반환: {}", user.getPoint());

        // 예약 작성자가 본인인지 확인
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("예약 삭제 권한이 없습니다.");
        }

        reservation.setCancelled(CancellationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    // 예약 수정 중간처리
    public void modify(String reservationId, ReservationSaveDto dto) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();

        // 기존 포인트 반환
        User user = reservation.getUser();
        user.addPoints((int) reservation.getPrice());
        log.info("기존 예약 취소 후 포인트 반환: {}", user.getPoint());

        // 날짜 중복 체크
        LocalDateTime startLocalDateTime = dto.getReservationAt().atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        LocalDateTime endLocalDateTime = dto.getReservationEndAt().atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();

        if (isReservationDateOverlap(dto.getRoomId(), startLocalDateTime, endLocalDateTime)) {
            throw new IllegalArgumentException("예약 날짜가 중복됩니다.");
        }

        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("예약 수정 권한이 없습니다.");
        }

        // 새로운 포인트 차감
        Room foundRoom = roomRepository.findById(dto.getRoomId()).orElseThrow();
        long newRoomPrice = foundRoom.getPrice();
        user.withdrawalPoints((int) newRoomPrice);
        log.info("새 예약으로 포인트 차감 후: {}", user.getPoint());

        // 예약 수정
        reservation.changeReservation(dto);
        reservation.setPrice(newRoomPrice);
        reservationRepository.save(reservation);

        // 사용자 정보 저장
        userRepository.save(user);
        log.info("수정된 예약 저장 후 포인트: {}", userRepository.findById(user.getId()).orElseThrow().getPoint());
    }
}
