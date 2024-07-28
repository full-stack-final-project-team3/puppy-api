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

    public LoginRequestDto(String email, String s) {
        this.email = email;
        this.password = s;
    }
    // 등등.. 추가
}
