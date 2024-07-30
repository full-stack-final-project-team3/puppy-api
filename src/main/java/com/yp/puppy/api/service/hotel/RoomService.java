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
        // 유저 확인
        User roomUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 관리자 권한 확인
        if (roomUser.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Only admins can register rooms.");
        }

        // 호텔 ID 확인 및 조회
        if (dto.getHotelId() == null || dto.getHotelId().trim().isEmpty()) {
            throw new IllegalArgumentException("Hotel ID must be provided.");
        }

        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid hotel ID: " + dto.getHotelId()));

        // Room 엔터티 생성 및 초기화
        Room newRoom = dto.toEntity();
        newRoom.setHotel(hotel);
        newRoom.setRoomUser(roomUser);

        // 이미지 처리
        if (dto.getRoomImages() != null && !dto.getRoomImages().isEmpty()) {
            List<HotelImage> images = dto.getRoomImages().stream()
                    .filter(imageDto -> imageDto.getHotelImgUri() != null && !imageDto.getHotelImgUri().isEmpty())
                    .map(imageDto -> {
                        HotelImage image = new HotelImage();
                        image.setHotelImgUri(imageDto.getHotelImgUri());
                        image.setType(imageDto.getType());
                        image.setRoom(newRoom); // 이 곳에서 객실과 이미지 연결
                        return image;
                    })
                    .collect(Collectors.toList());

            newRoom.setImages(images);
        }

        // 객실 정보 저장
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
