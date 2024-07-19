package com.yp.puppy.api.entity.user;


import com.yp.puppy.api.entity.hotel.Room;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString(exclude = "user")
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


    @Column(nullable = false, length = 20)
    private String dogName; // 강아지 이름

    @Column(nullable = false)
    private LocalDate birthday; // 강아지 생일

    @Setter
    private Breed dogBreed; // 견종

    @Setter
    private DogSize dogSize; // 소형견? 중형? 대형견?

    @Setter
    private double weight; // 강아지 몸무게

    @Setter
    private Sex dogSex; // 강아지 성별

    @Setter
    private boolean isNeutered; // 중성화 여부

    @Setter
    private Reason deleteReason; // 강아지 삭제 이유 (입양, 무지개다리 등)

    @Setter
    private boolean isDeleted; // true - 삭제함, false - 삭제안함

    @CreationTimestamp
    @Column(updatable = false) // 수정 불가
    private LocalDateTime createdAt; // 강아지 등록 일자

    @Transient
    // DB에 넣지않는 데이터
    private int age;


    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;



    @PrePersist
    private void prePersist() {
        if (this.age == 0) {
            this.age =  Math.abs((int) (this.getBirthday().getYear() - 2024));
        }
    }


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
