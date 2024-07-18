package com.yp.puppy.api.dto.request;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    private String email;
    private String password;

    // 등등.. 추가
}
