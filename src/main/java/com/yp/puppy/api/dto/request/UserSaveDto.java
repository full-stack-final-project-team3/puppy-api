package com.yp.puppy.api.dto.request;

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
}
