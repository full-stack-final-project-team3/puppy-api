package com.yp.puppy.api.dto.request.shop;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TreatsDetailPicDto {
    private MultipartFile treatsDetailPicFile; // MultipartFile
}

