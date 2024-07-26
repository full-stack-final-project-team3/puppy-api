package com.yp.puppy.api.entity.shop;

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

    @OneToOne
    @JoinColumn(name = "dog_id")
    private Dog dog;

    public void setDog(Dog dog) {
        this.dog = dog;
        if (dog != null) {
            dog.setBundle(this); // Dog의 bundle 필드도 설정
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

//    public void setBundleTitle(String bundleTitle) {
//        this.bundleTitle = "강아지 맞춤 간식 패키지"; // 고정된 값 할당
//    }
//
//    public void setBundlePrice(Long bundlePrice) {
//        this.bundlePrice = 29900L;
//    }
}