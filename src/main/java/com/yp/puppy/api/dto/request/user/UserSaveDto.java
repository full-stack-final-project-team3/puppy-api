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

    public UserSaveDto(String email, String password) {
        this.email = getEmail();
        this.password = getPassword();
    }

    // 더 받아야할 정보 추가 해야함!

}
