package com.yp.puppy.api.dto.request.user;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSaveDto {

    private String email;
    private String password;
    private String nickname;

    // 더 받아야할 정보 추가 해야함!

}
