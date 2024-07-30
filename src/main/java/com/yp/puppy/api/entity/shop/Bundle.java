package com.yp.puppy.api.entity.shop;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yp.puppy.api.dto.response.shop.TreatsInBundleDto;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"cart", "user", "dog"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bundle")
public class Bundle {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "bundle_id")
    private String id;

    @Column(nullable = false)
    private String bundleTitle; // 패키지 이름

    @Column(nullable = false)
    private Long bundlePrice; // 패키지 가격

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(
            name = "bundle_treats",
            joinColumns = @JoinColumn(name = "bundle_id"),
            inverseJoinColumns = @JoinColumn(name = "treats_id")
    )
//    @JsonManagedReference
    private List<Treats> treats = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Dog과의 관계
    @OneToOne
    @JoinColumn(name = "dog_id")
    @Setter
    @JsonIgnore
    private Dog dog;

    private SubsType subsType;

    private BundleStatus bundleStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private Cart cart;

    public enum SubsType {
        ONE, MONTH3, MONTH6
    }

    public enum BundleStatus {
        PENDING, ORDERED, CANCELLED
    }

//    public void setBundleTitle(String bundleTitle) {
//        this.bundleTitle = "강아지 맞춤 간식 패키지"; // 고정된 값 할당
//    }
//
//    public void setBundlePrice(Long bundlePrice) {
//        this.bundlePrice = 29900L;
//    }
}