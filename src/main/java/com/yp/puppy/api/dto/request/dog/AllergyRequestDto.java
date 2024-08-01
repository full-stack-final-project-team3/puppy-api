package com.yp.puppy.api.dto.request.dog;

import com.yp.puppy.api.entity.user.Dog;
import lombok.*;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllergyRequestDto {
    private List<Dog.Allergy> allergies;
    private String dogId;
}
