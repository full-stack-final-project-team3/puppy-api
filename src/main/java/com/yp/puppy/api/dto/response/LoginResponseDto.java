package com.yp.puppy.api.dto.response;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String email;
    private String role; // 권한
    private String token; // 인증 토큰
}
