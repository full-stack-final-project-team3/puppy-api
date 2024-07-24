package com.yp.puppy.api.dto.request.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import com.yp.puppy.api.entity.shop.Cart;
import com.yp.puppy.api.entity.shop.Treats;
import com.yp.puppy.api.entity.shop.TreatsDetailPic;
import com.yp.puppy.api.entity.shop.TreatsPic;
import com.yp.puppy.api.entity.user.Allergy;
import lombok.*;

import javax.persistence.Column;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartDto {

    @NotNull
    private Cart.SubsType subsType;

    @NotNull
    private Cart.CartStatus cartStatus;


}
