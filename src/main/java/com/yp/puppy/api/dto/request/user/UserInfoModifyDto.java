package com.yp.puppy.api.dto.request.user;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoModifyDto {
    private String email;
    private String nickname;
    private String password;
    private String address;
    private String phoneNumber;
}