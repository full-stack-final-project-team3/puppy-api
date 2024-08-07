package com.yp.puppy.api.dto.request.user;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSaveDto {

    private String email;
    private String password;
    private String nickname;
    private String address;
    private String phoneNumber;
    // 제공자
    private String provider;

    public UserSaveDto(String email, String password, String  nickname, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = "서울";
        this.provider = "KAKAO";
    }
    // 더 받아야할 정보 추가 해야함!

}
