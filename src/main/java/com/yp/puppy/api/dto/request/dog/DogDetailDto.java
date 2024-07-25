package com.yp.puppy.api.dto.request.dog;

import com.yp.puppy.api.entity.hotel.Room;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import lombok.Builder;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

import static com.yp.puppy.api.entity.user.Dog.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DogDetailDto {


    private String dogName; // 강아지 이름
    private LocalDate birthday; // 강아지 생일
    private Breed dogBreed; // 견종
    private DogSize dogSize; // 소형견? 중형? 대형견?
    private double weight; // 강아지 몸무게
    private Sex dogSex; // 강아지 성별
    private boolean isNeutered; // 중성화 여부
    private int age;


//    private boolean isDeleted; // true - 삭제함, false - 삭제안함
//    private Dog.Reason deleteReason; // 강아지 삭제 이유 (입양, 무지개다리 등)
//    private List<Allergy> allergies = new ArrayList<>(); // 리스트 초기화
//    private User user;
//    private Room room;
//    private Bundle bundle;


}
