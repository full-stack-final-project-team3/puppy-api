package com.yp.puppy.api.service.hotel;

import com.yp.puppy.api.dto.request.hotel.HotelSaveDto;
import com.yp.puppy.api.dto.response.hotel.HotelOneDto;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.user.Role;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.hotel.HotelRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    // 1. 호텔 전체조회 중간처리
    public void findAll() {
        hotelRepository.findAll();
    }


    // 2. 호텔 상세조회 중간처리
    public HotelOneDto getHotelDetail(Long hotelId) {

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow();

        return new HotelOneDto(hotel);

    }


    // 3. 호텔 생성 중간처리
    public void saveHotel(HotelSaveDto dto, String hotelId) {
        // 회원정보조회 (관리자냐?)
        User hotelUser = userRepository.findById(hotelId).orElseThrow();

        // 권한에 따른 글쓰기 제한
        if (hotelUser.getRole() != Role.ADMIN) throw new IllegalStateException("관리자만 등록을 할 수 있습니다.");

        Hotel newHotel = dto.toEntity();
        newHotel.setHotelUser(hotelUser);

        Hotel saveHotel = hotelRepository.save(newHotel);
        log.info("hotel: {}", saveHotel);
    }


    // 4. 호텔 삭제 중간처리
    public void deleteHotel(Long hotelId) {
        hotelRepository.deleteById(hotelId);
    }


    // 5. 호텔 수정 중간처리
    public void updateHotel(@RequestBody HotelSaveDto dto, @PathVariable Long hotelId) {
        Hotel foundHotel = hotelRepository.findById(hotelId).orElseThrow();
        foundHotel.changeHotel(dto);

        hotelRepository.save(foundHotel);

    }
}
