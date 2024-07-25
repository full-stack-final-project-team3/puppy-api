package com.yp.puppy.api.dto.request.shop;

import com.sun.istack.NotNull;
import com.yp.puppy.api.entity.shop.Cart;
import lombok.*;

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

//    @NotNull
//    private Cart.CartStatus cartStatus;


}
