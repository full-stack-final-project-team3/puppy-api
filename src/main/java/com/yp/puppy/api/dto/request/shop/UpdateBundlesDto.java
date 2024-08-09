package com.yp.puppy.api.dto.request.shop;

import com.sun.istack.NotNull;
import com.yp.puppy.api.entity.shop.Bundle;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBundlesDto {

    @NotNull
    private List<BundleUpdateDto> bundles; // 여러 개의 번들을 담는 리스트

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BundleUpdateDto {
        @NotNull
        private String bundleId;

        @NotNull
        private Bundle.SubsType subsType; // 구독 타입
    }
}
