package com.yp.puppy.api.dto.request.dog;

import com.yp.puppy.api.entity.user.Allergy;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.yp.puppy.api.entity.user.Dog.*;
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DogSaveDto {

    private String dogName;
    private Breed dogBreed;
    private LocalDate birthday;
    private Sex dogSex;
    private boolean isNeutered;
    private DogSize dogSize;
    private double weight;
    private User user; // 누구의 강아지인가
//    private List<String> allergies;

    public Dog toEntity() {
        Dog dog = Dog.builder()
                .dogName(this.dogName)
                .dogBreed(this.dogBreed)
                .birthday(this.birthday)
                .dogSex(this.dogSex)
                .dogSize(findDogSize(this.weight))
                .isNeutered(this.isNeutered)
                .weight(this.weight)
                .user(this.user)
//                .allergies(new ArrayList<>()) // 빈 리스트로 초기화
                .build();

//        for (String allergyType : this.allergies) {
//            Allergy allergy = Allergy.builder()
//                    .type(Allergy.AllergicType.valueOf(allergyType))
//                    .build();
//            dog.addAllergy(allergy);
//        }

        return dog;
    }

    private DogSize findDogSize(double weight) {
        if (weight > 25) {
            this.dogSize = DogSize.LARGE;
        } else if (weight > 10) {
            this.dogSize = DogSize.MEDIUM;
        } else if (weight > 1) {
            this.dogSize = DogSize.SMALL;
        }
        return dogSize;
    }

}