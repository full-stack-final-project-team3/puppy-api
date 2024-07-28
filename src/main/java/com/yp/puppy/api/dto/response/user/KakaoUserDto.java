package com.yp.puppy.api.dto.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoUserDto {

    private Long id;

    private Properties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter @ToString
    public static class Properties {
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;
    }

    @Getter @ToString
    public static class KakaoAccount {

        private String email;


    }
}