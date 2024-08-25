package com.yp.puppy.api.dto.response.hotel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoPayReadyResponse {
    private String tid;  // 결제 고유 번호
    private String next_redirect_pc_url;  // PC에서 결제 페이지로 리다이렉트할 URL
    private String next_redirect_mobile_url;  // 모바일에서 결제 페이지로 리다이렉트할 URL
    private String created_at;  // 결제 준비 요청 시간
}