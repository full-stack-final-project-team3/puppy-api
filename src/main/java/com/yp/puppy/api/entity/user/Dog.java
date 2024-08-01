package com.yp.puppy.api.entity.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yp.puppy.api.entity.hotel.Room;
import com.yp.puppy.api.entity.shop.Bundle;
import com.yp.puppy.api.entity.shop.Cart;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(exclude = {"user", "dogProfileUrl", "bundle"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dog")
public class Dog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "dog_id")
    private String id;

    @Column(nullable = false, length = 20)
    @Setter
    private String dogName; // 강아지 이름

    @Column(nullable = false)
    private LocalDate birthday; // 강아지 생일

    @Setter
    @Enumerated(EnumType.STRING)
    private Breed dogBreed; // 견종

    @Setter
    @Enumerated(EnumType.STRING)
    private DogSize dogSize; // 소형견? 중형? 대형견?

    @Setter
    private double weight; // 강아지 몸무게

    @Setter
    @Enumerated(EnumType.STRING)
    private Sex dogSex; // 강아지 성별

    @Setter
    private boolean isNeutered; // 중성화 여부

    @Setter
    private Reason deleteReason; // 강아지 삭제 이유 (입양, 무지개다리 등)

    @Setter
    private boolean isDeleted; // true - 삭제함, false - 삭제안함

    @Setter
    @Column(length = 500000)
    private String dogProfileUrl; // 강아지 사진 0725 추가

    @Setter
    @ElementCollection(targetClass = Allergy.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<Allergy> allergies = new ArrayList<>(); // 리스트 초기화

    @CreationTimestamp
    @Column(updatable = false) // 수정 불가
    private LocalDateTime createdAt; // 강아지 등록 일자

    private int age;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-dogs")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonIgnore
    private Room room;

    // Bundle과의 관계
    @OneToOne
    @JoinColumn(name = "bundle_id")
    @Setter
    @JsonIgnore
    private Bundle bundle;

//    @PrePersist
//    private void prePersist() {
//        if (this.age == 0) {
//            this.age = Math.abs((int) (this.getBirthday().getYear() - 2024));
//        }
//    }

    public enum DogSize {
        SMALL, MEDIUM, LARGE
    }

    public enum Sex {
        MALE, FEMALE, NEUTER
    }

    public enum Reason {
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
        WELSH_CORGI,
        SIBERIAN_HUSKY,
        DOBERMAN,
        SHIH_TZU,
        BOSTON_TERRIER,
        POMERANIAN,
        CHIHUAHUA,
        MALTESE,
        PIT_BULL,
        ROTTWEILER,
        SAINT_BERNARD,
        SAMOYED,
        JINDOTGAE,
    }

    public void addUser(User user) {
        this.user = user;
        user.addDog(this);
    }

    public enum Allergy {
        BEEF,
        CHICKEN,
        CORN,
        DAIRY,
        FISH,
        FLAX,
        LAMB,
        PORK,
        TURKEY,
        WHEAT,
        SOY,
        RICE,
        PEANUT,
        BARLEY,
        OAT,
        POTATO,
        TOMATO,
        SALMON,
        DUCK

    }

}