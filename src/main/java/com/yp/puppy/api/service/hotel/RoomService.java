package com.yp.puppy.api.service.hotel;

import com.yp.puppy.api.dto.request.hotel.RoomSaveDto;
import com.yp.puppy.api.dto.response.hotel.HotelDetailDto;
import com.yp.puppy.api.dto.response.hotel.HotelOneDto;
import com.yp.puppy.api.dto.response.hotel.RoomDetailDto;
import com.yp.puppy.api.dto.response.hotel.RoomOneDto;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.entity.hotel.HotelImage;
import com.yp.puppy.api.entity.hotel.Room;
import com.yp.puppy.api.entity.user.Role;
import com.yp.puppy.api.entity.user.User;
import com.yp.puppy.api.repository.hotel.HotelRepository;
import com.yp.puppy.api.repository.hotel.RoomRepository;
import com.yp.puppy.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;


    // 전체조회 중간처리
    public Map<String, Object> getRooms(int pageNo, String sort) {

        Pageable pageable = PageRequest.of(pageNo - 1, 4);

        Page<Room> roomPage = roomRepository.findRooms(pageable, sort);

        List<Room> roomList = roomPage.getContent();

        List<RoomDetailDto> roomDtoList = roomList.stream()
                .map(RoomDetailDto::new)
                .collect(Collectors.toList());

        // 렌더링 될 개수
        long totalElements = roomPage.getTotalElements();

        Map<String, Object> map = new HashMap<>();
        map.put("hotels", roomDtoList);
        map.put("totalCount", totalElements);

        return map;

    }


    public void saveRoom(RoomSaveDto dto, String userId) {
        // 회원 정보 조회
        User roomUser = userRepository.findById(userId).orElseThrow();

        // 권한 검사
        if (roomUser.getRole() != Role.ADMIN) {
            throw new IllegalStateException("관리자만 등록을 할 수 있습니다.");
        }

        // 호텔 ID 검사 및 호텔 조회
        if (dto.getHotelId() == null) {
            throw new IllegalArgumentException("Hotel ID가 제공되지 않았습니다.");
        }
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid hotel ID: " + dto.getHotelId()));

        // Room 엔티티 생성 및 저장
        Room newRoom = dto.toEntity();
        newRoom.setHotel(hotel);
        newRoom.setRoomUser(roomUser);


        if (dto.getRoomImage() != null) {
            List<HotelImage> images = dto.getRoomImage().stream()
                    .peek(image -> image.setRoom(newRoom))
                    .collect(Collectors.toList());
            newRoom.setImages(images);
        }
        roomRepository.save(newRoom);
    }


    // 객실 상세조회 중간처리
    public RoomOneDto getRoomDetail(String roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow();
        return new RoomOneDto(room);

    }

    // 삭제 중간처리
    public void deleteRoom(String roomId) {
        roomRepository.deleteById(roomId);
    }

    // 수정 중간처리
    public void modifyRoom(String roomId, RoomSaveDto dto) {
        Room foundRoom = roomRepository.findById(roomId).orElseThrow();
        foundRoom.changeRoom(dto);

        roomRepository.save(foundRoom);
    }
}
