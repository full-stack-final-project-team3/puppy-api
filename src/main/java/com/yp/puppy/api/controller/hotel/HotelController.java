package com.yp.puppy.api.controller.hotel;

import com.yp.puppy.api.auth.TokenProvider;
import com.yp.puppy.api.auth.TokenProvider.TokenUserInfo;
import com.yp.puppy.api.dto.request.hotel.HotelSaveDto;
import com.yp.puppy.api.dto.response.hotel.FavoriteDTO;
import com.yp.puppy.api.dto.response.hotel.HotelOneDto;
import com.yp.puppy.api.entity.hotel.Favorite;
import com.yp.puppy.api.entity.hotel.Hotel;
import com.yp.puppy.api.service.hotel.FavoriteService;
import com.yp.puppy.api.service.hotel.HotelService;
import com.yp.puppy.api.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hotel")
@Slf4j
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final FavoriteService favoriteService;

    @Value("${file.upload-dir}")
    private String uploadDir;


// 1. 호텔 전체 조회
    /**
     * @param sort 정렬 기준 (기본값 'name')
     * @param location 검색할 위치 (기본값은 빈 문자열)
     * @return 호텔 목록과 상태 코드
     */
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false, defaultValue = "name") String sort,
                                  @RequestParam(required = false, defaultValue = "") String location) {

        try {
            Map<String, Object> hotels = hotelService.getHotels(sort, location);
            if (hotels.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok().body(hotels);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching hotels.");
        }
    }



    // 2. 호텔 상세 조회
    /**
     * 호텔 상세 정보를 조회합니다.
     * @param hotelId URL 경로에서 제공된 호텔의 식별자
     * @return 호텔의 상세 정보를 반환합니다. 호텔 ID가 유효하지 않으면, 400 Bad Request 를 반환.
     */
    @GetMapping("/{hotelId}")
    public ResponseEntity<?> getHotel(@PathVariable String hotelId) {
        try {
            HotelOneDto hotelDetail = hotelService.getHotelDetail(hotelId);
            return ResponseEntity.ok().body(hotelDetail);
        } catch (Exception e) {
            log.warn("호텔 ID로 조회하는 중 오류 발생 : {}", hotelId);
            return ResponseEntity.badRequest().body("잘못된 hotelId 입니다.");
        }

    }


    // 3 호텔 생성
    /**
     * @param userInfo 사용자 정보
     * @param dto 호텔 저장 데이터 전송 객체
     * @return 생성 성공 메시지 또는 오류 메시지
     * @PreAuthorize 관리자만 작성가능
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<?> register(@AuthenticationPrincipal TokenUserInfo userInfo,
                                      @RequestBody HotelSaveDto dto) {
        try {
            Hotel newHotel = hotelService.saveHotel(dto, userInfo.getUserId());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "호텔 생성 성공");
            response.put("id", newHotel.getHotelId());
            return ResponseEntity.ok().body(response);
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    // 4. 호텔 삭제
    /**
     * @param hotelId 삭제할 호텔의 아이디
     * @return 삭제 성공 메시지 또는 오류 메시지
     * @PreAuthorize 관리자만 삭제가능
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<?> deleteHotel(@PathVariable String hotelId) {
        try {
            hotelService.deleteHotel(hotelId);
            return ResponseEntity.ok().body("삭제성공");
        } catch (Exception e) {
            log.warn("호텔 삭제에 실패했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("호텔을 찾지 못햇습니다..");
        }
    }


    // 5. 호텔 수정
    /**
     * @param dto 호텔 수정 정보를 담은 데이터 전송 객체
     * @param hotelId 수정할 호텔의 아이디
     * @return 수정 성공 메시지 또는 오류 메시지
     * @PreAuthorize 관리자만 수정 가능
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{hotelId}")
    public ResponseEntity<?> modify(@RequestBody HotelSaveDto dto,
                                        @PathVariable String hotelId) {
        try {
            hotelService.updateHotel(dto, hotelId);
            return ResponseEntity.ok().body("수정 성공");
        } catch (Exception e) {
            log.warn("수정에 실패했습니다.: {}", e.getMessage());
            return ResponseEntity.status(404).body("수정할 호텔을 찾지 못했습니다..");
        }
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
            }
            String uploadedFilePath = FileUtil.uploadFile(uploadDir, file);
            return ResponseEntity.ok(uploadedFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }


    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get("uploads").resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    // 6. 호텔 즐겨찾기 추가
    /**
     * @param userInfo 사용자 정보
     * @param hotelId 즐겨찾기할 호텔의 아이디
     * @return 즐겨찾기 추가 성공 메시지 또는 오류 메시지
     */
    @PostMapping("/{hotelId}/favorite")
    public ResponseEntity<?> addFavorite(@AuthenticationPrincipal TokenUserInfo userInfo, @PathVariable String hotelId) {
        try {
            String result = favoriteService.addFavorite(userInfo.getUserId(), hotelId);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("즐겨찾기가 이미 존재합니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("즐겨찾기 추가에 실패했습니다.");
        }
    }


    // 7. 호텔 즐겨찾기 제거
    /**
     * @param userInfo 사용자 정보
     * @param hotelId 즐겨찾기에서 제거할 호텔의 아이디
     * @return 즐겨찾기 제거 성공 메시지 또는 오류 메시지
     */
    @DeleteMapping("/{hotelId}/favorite")
    public ResponseEntity<?> removeFavorite(@AuthenticationPrincipal TokenUserInfo userInfo, @PathVariable String hotelId) {
        try {
            favoriteService.removeFavorite(userInfo.getUserId(), hotelId);
            return ResponseEntity.ok("즐겨찾기가 성공적으로 제거되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("즐겨찾기 제거에 실패했습니다.");
        }
    }

    // 8. 사용자 즐겨찾기 조회
    /**
     * @param userInfo 사용자 정보
     * @return 사용자의 즐겨찾기 목록과 상태 코드
     */
    @GetMapping("/favorites")
    public ResponseEntity<?> getUserFavorites(@AuthenticationPrincipal TokenUserInfo userInfo) {
        try {
            List<Favorite> favorites = favoriteService.getFavoritesByUser(userInfo.getUserId());
            List<FavoriteDTO> favoriteDTOs = favorites.stream()
                    .map(FavoriteDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(favoriteDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("즐겨찾기 조회에 실패했습니다.");
        }
    }



}
