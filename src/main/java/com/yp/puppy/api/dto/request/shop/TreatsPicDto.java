package com.yp.puppy.api.dto.request.shop;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TreatsPicDto {
    private MultipartFile treatsPicFile; // MultipartFile
}
