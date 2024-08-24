package com.yp.puppy.api.controller.hotel;

import com.yp.puppy.api.dto.request.hotel.PaymentRequestDto;
import com.yp.puppy.api.dto.response.hotel.KakaoPayReadyResponse;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class KakaoPayController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String KAKAO_API_KEY = "YOUR_KAKAO_API_KEY";

    @PostMapping("/kakao/ready")
    public ResponseEntity<?> kakaoPayReady(@RequestBody PaymentRequestDto paymentRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", UUID.randomUUID().toString());
        params.add("partner_user_id", paymentRequestDto.getUserId());
        params.add("item_name", "호텔 예약");
        params.add("quantity", "1");
        params.add("total_amount", String.valueOf(paymentRequestDto.getTotalPrice()));
        params.add("vat_amount", "0");
        params.add("tax_free_amount", "0");
        params.add("approval_url", "http://localhost:3000/payment/success");
        params.add("cancel_url", "http://localhost:3000/payment/cancel");
        params.add("fail_url", "http://localhost:3000/payment/fail");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<KakaoPayReadyResponse> response = restTemplate.postForEntity(
                    "https://kapi.kakao.com/v1/payment/ready",
                    request,
                    KakaoPayReadyResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 준비 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카카오페이 서버 오류: " + e.getMessage());
        }
    }
}
