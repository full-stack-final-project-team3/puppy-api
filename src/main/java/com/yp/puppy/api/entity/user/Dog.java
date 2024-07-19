package com.yp.puppy.api.entity.user;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "dog")
public class Dog {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "dog_id")
    private String id;

    // 컬럼 정의하고 테스트돌려보기

    @Column(nullable = false)
    private String dogName; // 강아지 이름

    private LocalDate birthday; // 강아지 생일

    private Breed dogBreed; // 견종

    private DogSize dogSize; // 소형견? 중형? 대형견?

    private double weight; // 강아지 몸무게

    private Sex dogSex; // 강아지 성별

    private boolean isNeutered; // 중성화 여부

    private Reason deleteReason; // 강아지 삭제 이유 (입양, 무지개다리 등)

    private boolean isDeleted; // true - 삭제함, false - 삭제안함

    private LocalDateTime createdAt; // 강아지 등록 일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    enum DogSize {
        SMALL, MEDIUM, LARGE
    }

    enum Sex {
        MALE, FEMALE
    }

    enum Reason {
        ADOPTION, // 입양
    }

    public enum Breed {
        RETRIEVER,
        SHEPHERD,
        BULLDOG,
        POODLE,
        BEAGLE,
        YORKSHIRE_TERRIER,
        DACHSHUND,
        PEMBROKE_WELSH_CORGI,
        SIBERIAN_HUSKY,
        DOBERMAN,
        SHIH_TZU,
        BOSTON_TERRIER,
        POMERANIAN,
        // ...
    }
}
