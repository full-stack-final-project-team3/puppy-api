package com.yp.puppy.api.dto.response.user;

import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.Role;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = "profileUrl")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 마이페이지에 들어갈 세부적인 유저 정보를 담고있는 dto
public class UserResponseDto {

    private String id;
    private String email;
    private Role role;
    private String nickname;
    private LocalDate birthday;
    private String phoneNumber;
    private Integer point;
    private String password; // - 0807 추가
    private String address;
    private String realName;
    private String profileUrl;
    private boolean hasDogInfo;
    private int warningCount;
    private List<Dog> dogList;
    private int noticeCount;

    // 회원가입 제공자
    private String provider;


}
