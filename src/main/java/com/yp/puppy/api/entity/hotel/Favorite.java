package com.yp.puppy.api.entity.hotel;

import com.yp.puppy.api.entity.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@ToString(exclude = {"favoriteId", "user", "hotel"})
@EqualsAndHashCode(of = "favoriteId")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "favorites") // 테이블 이름 수정

public class Favorite {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "favorite_id")
    private String favoriteId; // 북마크 아이디 (UUID)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel; // 호텔
}
