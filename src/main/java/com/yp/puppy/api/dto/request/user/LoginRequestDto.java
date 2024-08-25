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
    private String address;
    private boolean autoLogin;
    private String detailAddress;

    public LoginRequestDto(String email, String password, String nickname, String phoneNumber, String address, String detailAddress, boolean autoLogin) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.detailAddress = detailAddress;
        this.autoLogin = autoLogin;
    }
    // 등등.. 추가
}
