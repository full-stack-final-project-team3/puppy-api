package com.yp.puppy.api.dto.request.user;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    private String email;
    private String password;
    private boolean autoLogin;
    // 등등.. 추가
}
