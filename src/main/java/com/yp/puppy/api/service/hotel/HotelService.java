package com.yp.puppy.api.service.hotel;

import com.yp.puppy.api.dto.request.hotel.HotelSaveDto;
import com.yp.puppy.api.dto.response.hotel.HotelDetailDto;
import com.yp.puppy.api.dto.response.hotel.HotelOneDto;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.user.Role;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.hotel.HotelRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    // 1. 호텔 전체조회 중간처리
    @Transactional(readOnly = true)
    public Map<String, Object> getHotels(String sort, String location) {
        Sort sortOrder = Sort.by(sort).ascending();
        List<Hotel> hotels = hotelRepository.findHotels(location, sortOrder);

        List<HotelDetailDto> hotelDtoList = hotels.stream()
                .map(HotelDetailDto::new)
                .collect(Collectors.toList());

        // 렌더링 될 개수
        long totalElements = hotels.size();

        Map<String, Object> map = new HashMap<>();
        map.put("hotels", hotelDtoList);
        map.put("totalCount", totalElements);

        return map;
    }


    // 2. 호텔 상세조회 중간처리
    public HotelOneDto getHotelDetail(String hotelId) {

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow();

        return new HotelOneDto(hotel);

    }


    // 3. 호텔 생성 중간처리
    public Hotel saveHotel(HotelSaveDto dto, String userId) {
        User hotelUser = userRepository.findById(userId).orElseThrow();
        if (hotelUser.getRole() != Role.ADMIN) throw new IllegalStateException("관리자만 등록을 할 수 있습니다.");

        Hotel newHotel = dto.toEntity();
        newHotel.setHotelUser(hotelUser);

        Hotel savedHotel = hotelRepository.save(newHotel);
        log.info("hotel: {}", savedHotel);
        return savedHotel; // 반환값 변경
    }


    // 4. 호텔 삭제 중간처리
    public void deleteHotel(String hotelId) {
        hotelRepository.deleteById(hotelId);
    }


    // 5. 호텔 수정 중간처리
    public void updateHotel(@RequestBody HotelSaveDto dto, @PathVariable String hotelId) {
        Hotel foundHotel = hotelRepository.findById(hotelId).orElseThrow();
        foundHotel.changeHotel(dto);

        hotelRepository.save(foundHotel);

    }

}
