package com.yp.puppy.api.dto.response.dog;

import com.yp.puppy.api.entity.user.Dog;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 강아지 정보를 렌더링할때 사용할 dto
public class DogResponseDto {

    private String id;
    private String dogName;
    private LocalDate birthday; // 강아지 생일
    private Dog.Breed dogBreed; // 견종
    private Dog.DogSize dogSize; // 소형견? 중형? 대형견?
    private double weight;
    private Dog.Sex dogSex;
    private boolean isNeutered;
    private String dogProfileUrl;
    private List<Dog.Allergy> allergies = new ArrayList<>();
    private LocalDateTime createdAt; // 강아지 등록 일자
    private int age;


    public DogResponseDto toEntity(Dog dog) {

        DogResponseDto dto = DogResponseDto.builder()
                .id(dog.getId())
                .dogName(dog.getDogName())
                .birthday(dog.getBirthday())
                .dogBreed(dog.getDogBreed())
                .dogSize(dog.getDogSize())
                .weight(dog.getWeight())
                .dogSex(dog.getDogSex())
                .isNeutered(dog.isNeutered())
                .dogProfileUrl(dog.getDogProfileUrl())
                .allergies(dog.getAllergies())
                .createdAt(dog.getCreatedAt())
                .age(dog.getAge())
                .build();
        return dto;
    }




}
