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
    public Map<String, Object> getHotels(int pageNo, String sort) {
        Pageable pageable = PageRequest.of(pageNo - 1, 4);

        Page<Hotel> hotelPage = hotelRepository.findHotels(pageable, sort);

        List<Hotel> hotelList = hotelPage.getContent();

        List<HotelDetailDto> hotelDtoList = hotelList.stream()
                .map(HotelDetailDto::new)
                .collect(Collectors.toList());

        // 렌더링 될 개수
        long totalElements = hotelPage.getTotalElements();

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
    public void saveHotel(HotelSaveDto dto, String userId) {
        // 회원정보조회 (관리자냐?)
        User hotelUser = userRepository.findById(userId).orElseThrow();

        // 권한에 따른 글쓰기 제한
        if (hotelUser.getRole() != Role.ADMIN) throw new IllegalStateException("관리자만 등록을 할 수 있습니다.");

        Hotel newHotel = dto.toEntity();
        newHotel.setHotelUser(hotelUser);

        Hotel saveHotel = hotelRepository.save(newHotel);
        log.info("hotel: {}", saveHotel);
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
