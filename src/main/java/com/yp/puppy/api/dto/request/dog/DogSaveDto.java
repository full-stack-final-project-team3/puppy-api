package com.yp.puppy.api.dto.request.dog;

import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

import static com.yp.puppy.api.entity.user.Dog.*;
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DogSaveDto {

    private static final Logger log = LoggerFactory.getLogger(DogSaveDto.class);
    private String dogName;
    private Breed dogBreed;
    private LocalDate birthday;
    private Sex dogSex;
    private boolean isNeutered;
    private DogSize dogSize;
    private double weight;
    private User user; // 누구의 강아지인가
    private List<Dog.Allergy> allergies;
    private String dogProfileUrl;
    private int age;

    public Dog toEntity(User user) {
        Dog dog = Dog.builder()
                .dogName(this.dogName)
                .dogBreed(this.dogBreed)
                .birthday(this.birthday)
                .dogSex(this.dogSex)
                .dogSize(findDogSize(this.weight))
                .isNeutered(this.isNeutered)
                .dogProfileUrl(this.dogProfileUrl)
                .weight(this.weight)
                .age(decideDogAge(this.birthday))
                .user(user)
                .allergies(this.allergies) // 빈 리스트로 초기화
                .build();
        return dog;
    }

    private int decideDogAge(LocalDate birthday) {
        log.info("시발 왜 안됨? - {} ", birthday);
        this.age = Math.abs((int) (this.getBirthday().getYear() - 2024));
        log.info("age : {}", this.age);
        return age;
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