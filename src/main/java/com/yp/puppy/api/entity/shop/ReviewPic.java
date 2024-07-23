package com.yp.puppy.api.entity.shop;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "review_pic")
public class ReviewPic {

    @Id
//    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
//    @GeneratedValue(generator = "uuid-generator")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "review_pic_id")
    private String id;

    private String reviewPic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;
}
