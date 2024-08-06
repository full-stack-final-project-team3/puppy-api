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
    private String nickname;
    private String phoneNumber;
    private boolean autoLogin;

    public LoginRequestDto(String email, String s, String nickname, String phoneNumber) {
        this.email = email;
        this.password = s;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }
    // 등등.. 추가
}
