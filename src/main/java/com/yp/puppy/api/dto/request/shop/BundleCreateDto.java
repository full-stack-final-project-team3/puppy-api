package com.yp.puppy.api.dto.request.shop;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BundleCreateDto {

//    private String dogId;
    private List<String> treatIds;

}
