package com.yp.puppy.api.entity.shop;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yp.puppy.api.entity.user.Dog;
import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"treats", "cart"})
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

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Treats> treats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Dog과의 관계
    @OneToOne
    @JoinColumn(name = "dog_id")
    @Setter
    private Dog dog;

    private SubsType subsType;

    private BundleStatus bundleStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
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