package com.yp.puppy.api.dto.request.shop;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Builder
public class ReviewSaveDto {
    private String reviewContent;
    private int rate;
    private String userId;
    private String treatsId;

    // ReviewSaveDto 클래스 내에 추가
    @JsonIgnore
    private LocalDateTime createdAt;

    //이미지 업로드 추가
    private List<MultipartFile> reviewPics;



}
