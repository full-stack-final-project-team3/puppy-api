package com.yp.puppy.api.dto.request.shop;

import com.sun.istack.NotNull;
import com.yp.puppy.api.entity.shop.Bundle;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBundleDto {

    @NotNull
    private String bundle_id;

    @NotNull
    private Bundle.SubsType subsType;

}
