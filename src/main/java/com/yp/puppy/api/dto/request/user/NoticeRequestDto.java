package com.yp.puppy.api.dto.request.user;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeRequestDto {
    private String userId;
    private String message;
}
