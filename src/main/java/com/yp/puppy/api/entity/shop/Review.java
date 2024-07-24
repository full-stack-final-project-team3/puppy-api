package com.yp.puppy.api.entity.shop;

import com.yp.puppy.api.entity.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "ShopReview") // 엔티티 이름 명시
@Table(name = "review")
public class Review {

    @Id
//    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
//    @GeneratedValue(generator = "uuid-generator")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "review_id")
    private String id;

    @Column(nullable = false)
    private String reviewContent;

    @Column(nullable = false)
    private int rate; // 별점

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewPic> reviewPics;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treats_id", nullable = false)
    private Treats treats;
}